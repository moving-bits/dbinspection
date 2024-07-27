package net.movingbits.dbinspection;

import net.movingbits.datatables.adapters.BaseTableAdapter;
import net.movingbits.datatables.Datatable;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class DBInspectionBaseActivity extends AppCompatActivity {

    protected static final String BUNDLE_CURRENTTABLE = "BUNDLE_DBI_currentTable";
    protected static final String BUNDLE_SORTCOLUMN = "BUNDLE_DBI_sortColumn";
    protected static final String BUNDLE_SORTASCENDING = "BUNDLE_DBI_sortAscending";
    protected static final String BUNDLE_OFFSET = "BUNDLE_DBI_offset";
    protected static final String BUNDLE_SEARCHTERM = "BUNDLE_DBI_searchTerm";

    protected SQLiteDatabase database;
    protected String currentTable = "";
    private Datatable dbInspectionTable;
    protected TableInfo tableInfo;
    protected final ArrayList<ArrayList<String>> tableData = new ArrayList<>();
    protected String sortColumn = "";
    protected boolean sortAscending = true;
    protected int itemsPerPage = 10;
    protected int offset = 0;
    private String searchTerm;
    private Bundle savedInstanceState = null;

    // configurable items
    protected int colorHeaderBackgroundColor = 0xC0FFFFFF;
    protected int getColorHeaderBackgroundColorPK = Color.GRAY;
    protected int colorHeaderTextColor = Color.BLACK;
    protected int colorCellBackgroundColorOdd = 0xC0AAAAAA;
    protected int colorCellBackgroundColorEven = 0xC0666666;
    protected int colorCellTextColor = Color.WHITE;

    protected int pxMargin = 10;
    protected int pxCharWidth = 22;
    protected int pxHeight = 40;

    protected String titleSelectTable = "Select table";

    protected enum StorageClass {
        STORAGE_NULL(new String[]{"", "NULL"}),
        STORAGE_INTEGER(new String[]{"INTEGER", "LONG"}),
        STORAGE_REAL(new String[]{"REAL", "FLOAT", "DOUBLE"}),
        STORAGE_TEXT(new String[]{"TEXT", "VARCHAR"}),
        STORAGE_BLOB(new String[]{"BLOB"});

        final String[] hints;

        static StorageClass getStorageClass(final String hint) {
            for (StorageClass storageClass : values()) {
                if (StringUtils.equalsAnyIgnoreCase(hint, storageClass.hints)) {
                    return storageClass;
                }
            }
            return STORAGE_NULL;
        }

        StorageClass(final String[] hints) {
            this.hints = hints;
        }
    }

    protected static class ColumnInfo {
        public final int position;
        public final String name;
        public final String type;
        public final boolean notNull;
        public final String defaultValue;
        public final int primaryKeyPosition;
        public final StorageClass storageClass;

        ColumnInfo(final int position, final String name, final String type, final boolean notNull, final String defaultValue, final int primaryKeyPosition, final StorageClass storageClass) {
            this.position = position;
            this.name = name;
            this.type = type;
            this.notNull = notNull;
            this.defaultValue = defaultValue;
            this.primaryKeyPosition = primaryKeyPosition;
            this.storageClass = storageClass;
        }
    }

    protected static class TableInfo {
        final String name;
        List<ColumnInfo> columns;
        boolean hasPrimaryKey;

        TableInfo(final String name) {
            this.name = name;
            this.columns = new ArrayList<>();
            this.hasPrimaryKey = false;
        }
    }

    @Override
    public void onCreate(final @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remember for later use in updateTableData
        this.savedInstanceState = savedInstanceState;
    }

    protected void prepareBlankTable(final int tableId) {
        // prepare dummy table
        tableInfo = new TableInfo(currentTable);
        tableInfo.columns.add(new ColumnInfo(0, "", "INT", false, "0", 0, StorageClass.STORAGE_INTEGER));
        dbInspectionTable = findViewById(tableId);
        dbInspectionTable.setAdapter(new DBInspectionAdapter(this));
    }

    /** updates table data; set {@code resetToTable} to reset table name, search and sort options */
    protected boolean updateTableData(@Nullable final String resetToTable) {
        if (savedInstanceState != null && (StringUtils.isBlank(resetToTable) || StringUtils.equals(resetToTable, savedInstanceState.getString(BUNDLE_CURRENTTABLE)))) {
            if (StringUtils.isNotBlank(resetToTable)) {
                currentTable = resetToTable;
            }
            sortColumn = savedInstanceState.getString(BUNDLE_SORTCOLUMN);
            sortAscending = savedInstanceState.getBoolean(BUNDLE_SORTASCENDING);
            offset = savedInstanceState.getInt(BUNDLE_OFFSET);
            setSearchTerm(savedInstanceState.getString(BUNDLE_SEARCHTERM));
            savedInstanceState = null;
        } else if (StringUtils.isNotBlank(resetToTable) && !StringUtils.equals(resetToTable, currentTable)) {
            currentTable = resetToTable;
            sortColumn = "";
            sortAscending = true;
            setSearchTerm("");
        }

        tableInfo = getTableInfo(currentTable);
        tableData.clear();
        boolean moreDataAvailable = false;

        final ArrayList<String> paramsAL = new ArrayList<>();
        final StringBuilder whereCondition = new StringBuilder();
        if (StringUtils.isNotBlank(searchTerm)) {
            for (ColumnInfo column : tableInfo.columns) {
                paramsAL.add('%' + searchTerm + '%');
                whereCondition.append(" OR ").append(column.name).append(" LIKE ?");
            }
        }
        String[] params = new String[paramsAL.size()];
        params = paramsAL.toArray(params);

        try (Cursor temp = database.rawQuery("SELECT * FROM " + currentTable
                + (StringUtils.isNotBlank(whereCondition) ? " WHERE (0=1)" + whereCondition : "")
                + (StringUtils.isNotBlank(sortColumn) ? " ORDER BY `" + sortColumn + "` " + (sortAscending ? "ASC" : "DESC") : "")
                + " LIMIT " + offset + "," + (itemsPerPage + 1), params)) {
            if (temp.moveToFirst()) {
                int rowsCounted = 0;
                do {
                    final ArrayList<String> tempRow = new ArrayList<>();
                    for (int col = 0; col < tableInfo.columns.size(); col++) {
                        tempRow.add(temp.getString(col));
                    }
                    tableData.add(tempRow);
                    rowsCounted++;
                } while (temp.moveToNext() && rowsCounted < itemsPerPage);
            }
            moreDataAvailable = (temp.getCount() > itemsPerPage);
        }
        dbInspectionTable.setAdapter(new DBInspectionAdapter(this));
        return moreDataAvailable;
    }

    /**
     * read column names from given table
     */
    @SuppressLint("Range")
    private TableInfo getTableInfo(final String table) {
        final TableInfo tableInfo = new TableInfo(table);
        final List<ColumnInfo> tempColumns = new ArrayList<>();
        int minPK = -1;
        int maxPK = -1;

        try (Cursor temp = database.rawQuery("SELECT *, typeof(type) storageclass FROM pragma_table_info(?)", new String[]{ table })) {
            if (temp.moveToFirst()) {
                final int idxPosition = 0;
                final int idxName = temp.getColumnIndex("name");
                final int idxType = temp.getColumnIndex("type");
                final int idxNotNull = temp.getColumnIndex("notnull");
                final int idxDefaultValue = temp.getColumnIndex("dflt_value");
                final int idxPrimaryKeyPosition = temp.getColumnIndex("pk");
                final int idxStorageClass = temp.getColumnIndex("storageclass");

                do {
                    final int primaryKeyPosition = temp.getInt(idxPrimaryKeyPosition);
                    if (primaryKeyPosition > 0) {
                        tableInfo.hasPrimaryKey = true;
                        minPK = (minPK == -1 ? primaryKeyPosition : Math.min(minPK, primaryKeyPosition));
                        maxPK = (maxPK == -1 ? primaryKeyPosition : Math.max(maxPK, primaryKeyPosition));
                    }
                    final String type = temp.getString(idxType);
                    final StorageClass storageClass1 = StorageClass.getStorageClass(type);
                    final StorageClass storageClass2 = storageClass1 == StorageClass.STORAGE_NULL ? StorageClass.getStorageClass(temp.getString(idxStorageClass)) : storageClass1;
                    tempColumns.add(new ColumnInfo(temp.getInt(idxPosition), temp.getString(idxName), temp.getString(idxType), temp.getInt(idxNotNull) < 1, temp.getString(idxDefaultValue), primaryKeyPosition, storageClass2));
                } while (temp.moveToNext());
            }
        }
        // now create sorted column list, having fields with primaryKeyPosition first
        if (tableInfo.hasPrimaryKey) {
            for (int i = minPK; i <= maxPK; i++) {
                for (ColumnInfo info : tempColumns) {
                    if (info.primaryKeyPosition == i) {
                        tableInfo.columns.add(info);
                    }
                }
            }
            for (ColumnInfo info : tempColumns) {
                if (info.primaryKeyPosition == 0) {
                    tableInfo.columns.add(info);
                }
            }
        } else {
            tableInfo.columns = tempColumns;
        }

        return tableInfo;
    }

    /**
     * read table names from current database
     */
    protected List<String> getTablenames() {
        final List<String> categories = new ArrayList<>();
        categories.add(titleSelectTable);
        try (Cursor temp = database.rawQuery("SELECT name FROM sqlite_master WHERE TYPE IN ('table') AND name NOT LIKE 'sqlite_%' ORDER BY name", null)) {
            if (temp.moveToFirst()) {
                final int nameIdx = temp.getColumnIndex("name");
                if (nameIdx >= 0) {
                    do {
                        categories.add(temp.getString(nameIdx));
                    } while (temp.moveToNext());
                }
            }
        } catch (SQLiteException ignore) {
            return Collections.emptyList();
        }
        return categories;
    }

    /** will be called on long-tapping on column header */
    protected boolean onColumHeaderLongClickListener(final ColumnInfo columnInfo) {
        return false;
    }

    /** will be called on long-tapping on a field */
    protected boolean onFieldLongClickListener(final ColumnInfo columnInfo, final int row, final int inputType, final String currentValue, final boolean isPartOfPrimaryKey) {
        return false;
    }

    /** try to persist given data, returns true on success */
    protected boolean persistData(final int row, final String columnName, final String newValue) {
        if (!tableInfo.hasPrimaryKey) {
            return false; // cannot update content reliably without having a primary key
        }
        if (row < offset || row >= (offset + itemsPerPage)) {
            return false; // invalid offset given
        }
        for (ColumnInfo columnInfo : tableInfo.columns) {
            if (StringUtils.equals(columnName, columnInfo.name) && columnInfo.primaryKeyPosition == 0) {
                // build WHERE condition from primary key
                final ArrayList<String> whereValues = new ArrayList<>();
                final StringBuilder whereSQL = new StringBuilder();
                final ArrayList<String> currentValues = tableData.get(row - offset);
                for (ColumnInfo temp : tableInfo.columns) {
                    if (temp.primaryKeyPosition > 0) {
                        whereSQL.append(whereSQL.length() > 0 ? " AND " : "").append(temp.name).append(" = ?");
                        whereValues.add(currentValues.get(temp.position));
                    }
                }
                if (whereValues.isEmpty()) {
                    return false; // no pk found (should never happen)
                }
                // finalize query
                final ContentValues cv = new ContentValues(1);
                cv.put(columnInfo.name, newValue);
                String[] params = new String[whereValues.size()];
                params = whereValues.toArray(params);
                final long result = database.update(tableInfo.name, cv, whereSQL.toString(), params);
                if (result == 1) {
                    currentValues.set(columnInfo.position, newValue);
                }
                return result == 1;
            }
        }
        return false; // given column not found or part of a primary key
    }

    protected void setSearchTerm(final String newSearchTerm) {
        searchTerm = newSearchTerm;
    }

    protected String getSearchTerm() {
        return searchTerm;
    }

    private class DBInspectionAdapter extends BaseTableAdapter {

        private final int[] widths;
        final LayoutInflater inflater;

        private static final int VIEWTYPE_HEADER_PRIMARYKEY = 0;
        private static final int VIEWTYPE_HEADER_NONPK = 1;
        private static final int VIEWTYPE_DATA_EVEN = 2;
        private static final int VIEWTYPE_DATA_ODD = 3;

        DBInspectionAdapter(final Context context) {
            this.inflater = LayoutInflater.from(context);

            // calculate column widths in characters
            final int numCols = tableInfo.columns.size();
            widths = new int[numCols];
            int i = 0;
            for (ColumnInfo info : tableInfo.columns) {
                widths[i++] = info.name.length();
            }
            for (ArrayList<String> item : tableData) {
                for (int j = 0; j < numCols; j++) {
                    final String temp = item.get(j);
                    widths[j] = Math.max(widths[j], temp == null ? 0 : temp.length());
                }
            }
            // now apply scaling + convert to pixels
            final int nonBreakingWidth = 15;
            for (int j = 0; j < numCols; j++) {
                widths[j] = pxMargin + pxCharWidth * (widths[j] <= nonBreakingWidth ? widths[j] : nonBreakingWidth + (int) Math.round(Math.log(widths[j]) / Math.log(2)));
            }
        }

        @Override
        public int getRowCount() {
            return tableData.size();
        }

        @Override
        public int getColumnCount() {
            return tableInfo.columns.size();
        }

        @Override
        public int getWidth(final int column) {
            return widths[Math.max(column, 0)];
        }

        @Override
        public int getHeight(final int row) {
            return row < 0 ? pxHeight / 2 : pxHeight;
        }

        public String getCellString(final int row, final int column) {
            return column < 0
                    ? row < 0 ? "#" : (offset + row) + ":"
                    : row < 0 ? tableInfo.columns.get(column).name : tableData.get(row).get(column);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(final int row, final int column, final View convertView, final ViewGroup parent) {
            final LinearLayout v;
            final TextView tv;
            final int viewType = getItemViewType(row, column);
            if (convertView == null) {
                v = new LinearLayout(inflater.getContext());
                final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                lp.setMargins(1, 2, 1, 2);
                v.setLayoutParams(lp);
                v.setGravity(Gravity.LEFT);
                v.setOrientation(LinearLayout.HORIZONTAL);
                v.setPaddingRelative(1, 2, 1, 2);
                final int bg = viewType == VIEWTYPE_HEADER_PRIMARYKEY ? getColorHeaderBackgroundColorPK : viewType == VIEWTYPE_HEADER_NONPK ? colorHeaderBackgroundColor : viewType == VIEWTYPE_DATA_EVEN ? colorCellBackgroundColorEven : colorCellBackgroundColorOdd;
                v.setBackgroundColor(bg);

                tv = new TextView(inflater.getContext());
                tv.setId(android.R.id.text1);
                tv.setTextColor(row < 0 ? colorHeaderTextColor : colorCellTextColor);
                tv.setBackgroundColor(bg);
                tv.setLayoutParams(lp);
                v.addView(tv);

            } else {
                v = (LinearLayout) convertView;
                tv = v.findViewById(android.R.id.text1);
            }
            tv.setText(getCellString(row, column));

            if (row < 0 && column >= 0) {
                final ColumnInfo info = tableInfo.columns.get(column);
                if (StringUtils.equals(sortColumn, info.name)) {
                    tv.setText(tv.getText() + (sortAscending ? "↑" : "↓"));
                }
                // change sorting by header short tap
                tv.setOnClickListener(v1 -> {
                    offset = 0;
                    if (StringUtils.equals(sortColumn, info.name)) {
                        sortAscending = !sortAscending;
                    } else {
                        sortColumn = info.name;
                        sortAscending = true;
                    }
                    updateTableData(null);
                });
                // field info on header long tap
                tv.setOnLongClickListener(v1 -> DBInspectionBaseActivity.this.onColumHeaderLongClickListener(info));
            } else if (row >= 0 && column >= 0) {
                // edit data (if not part of primary key)
                final ColumnInfo info = tableInfo.columns.get(column);
                tv.setOnLongClickListener(v1 -> {
                    int inputType = 0;
                    switch (info.storageClass) {
                        case STORAGE_INTEGER:
                            inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL | InputType.TYPE_NUMBER_FLAG_SIGNED;
                            break;
                        case STORAGE_REAL:
                            inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL | InputType.TYPE_NUMBER_FLAG_DECIMAL;
                            break;
                        default:
                            inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL;
                            if (tv.getText().length() > 50) {
                                inputType |= InputType.TYPE_TEXT_FLAG_MULTI_LINE;
                            }
                            break;
                    }
                    return DBInspectionBaseActivity.this.onFieldLongClickListener(info, offset + row, inputType, String.valueOf(tv.getText()), info.primaryKeyPosition != 0);
                });
            }

            return v;
        }

        @Override
        public int getItemViewType(final int row, final int column) {
            final ColumnInfo info = row >= 0 || column < 0 || column >= tableInfo.columns.size() ? null : tableInfo.columns.get(column);
            return row < 0
                    ? info != null && info.primaryKeyPosition > 0 ? VIEWTYPE_HEADER_PRIMARYKEY : VIEWTYPE_HEADER_NONPK
                    : row % 2 == 0 ? VIEWTYPE_DATA_EVEN : VIEWTYPE_DATA_ODD;
        }

        @Override
        public int getViewTypeCount() {
            return 4;
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_CURRENTTABLE, currentTable);
        outState.putString(BUNDLE_SORTCOLUMN, sortColumn);
        outState.putBoolean(BUNDLE_SORTASCENDING, sortAscending);
        outState.putInt(BUNDLE_OFFSET, offset);
        outState.putString(BUNDLE_SEARCHTERM, searchTerm);
    }
}
