package net.movingbits.testapplication;

import net.movingbits.dbinspection.DBInspectionBaseActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.apache.commons.lang3.StringUtils;

/**
 * Sample application for DBInspectionBaseActivity
 * (c) 2024 moving-bits (<a href="https://github.com/moving-bits">moving-bits</a>)
 */

public class MainActivity extends DBInspectionBaseActivity implements AdapterView.OnItemSelectedListener {

    @Override
    public void onCreate(final @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dbinspection_activity);
        prepareBlankTable(R.id.tableView);
        database = DataStore.getDatabase(this);

        // set configurable items
        pxMargin = dpToPixel(10);
        pxCharWidth = dpToPixel(10);
        pxHeight = dpToPixel(40);
        titleSelectTable = getString(R.string.title_select_table);

        // initialize table selector
        final AppCompatSpinner spinner = findViewById(R.id.tableSpinner);
        spinner.setOnItemSelectedListener(this);
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getTablenames());
        spinner.setAdapter(spinnerAdapter);

        // prepare UI elements
        findViewById(R.id.tableButtonBack).setEnabled(false);
        findViewById(R.id.tableButtonBack).setOnClickListener(v -> pagination(offset - itemsPerPage));
        findViewById(R.id.tableButtonBack).setOnLongClickListener(v -> pagination(offset - 5 * itemsPerPage));
        final View.OnClickListener editSearch = v -> input(MainActivity.this, getString(R.string.title_search), getSearchTerm(), InputType.TYPE_CLASS_TEXT, n -> {
            final String newSearchTerm = n.trim();
            if (!StringUtils.equals(newSearchTerm, getSearchTerm())) {
                setSearchTerm(newSearchTerm);
                updateTableData(null);
            }
        });
        findViewById(R.id.tableButtonSearch).setEnabled(false);
        findViewById(R.id.tableButtonSearch).setOnClickListener(editSearch);
        findViewById(R.id.searchTerm).setOnClickListener(editSearch);

        findViewById(R.id.tableButtonForward).setEnabled(false);
        findViewById(R.id.tableButtonForward).setOnClickListener(v -> pagination(offset + itemsPerPage));
        findViewById(R.id.tableButtonForward).setOnLongClickListener(v -> pagination(offset + 5 * itemsPerPage));
    }

    private boolean pagination(final int newOffset) {
        offset = Math.max(0, newOffset);
        updateTableData(null);
        return true;
    }

    /**
     * On selecting a spinner item
     */
    @SuppressLint("Range")
    @Override
    public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
        final String item = parent.getItemAtPosition(position).toString();
        if (StringUtils.isBlank(item) || StringUtils.equals(item, titleSelectTable)) {
            onNothingSelected(parent);
            return;
        }
        updateTableData(item);
    }

    @Override
    protected boolean updateTableData(@Nullable final String resetToTable) {
        final boolean moreDataAvailable = super.updateTableData(resetToTable);
        findViewById(R.id.tableButtonBack).setEnabled(offset > 0);
        findViewById(R.id.tableButtonSearch).setEnabled(true);
        findViewById(R.id.tableButtonForward).setEnabled(moreDataAvailable);
        return moreDataAvailable;
    }

    @Override
    public void onNothingSelected(final AdapterView<?> parent) {
        Toast.makeText(parent.getContext(), R.string.error_no_table_selected, Toast.LENGTH_LONG).show();
    }

    @Override
    protected boolean onColumHeaderLongClickListener(final ColumnInfo columnInfo) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.columnproperties_title)
                .setMessage(String.format(getString(R.string.columnproperties_message), columnInfo.name, columnInfo.type, columnInfo.storageClass))
                .create();
        dialog.show();
        return true;
    }

    @Override
    protected boolean onFieldLongClickListener(final ColumnInfo columnInfo, final int row, final int inputType, final String currentValue, final boolean isPartOfPrimaryKey) {
        if (isPartOfPrimaryKey) {
            Toast.makeText(this, String.format(getString(R.string.error_pkfield), columnInfo.name), Toast.LENGTH_SHORT).show();
            return true;
        }
        input(this, String.format(getString(R.string.title_edit), columnInfo.name, row), currentValue, inputType, newValue -> {
            if (persistData(row, columnInfo.name, newValue)) {
                Toast.makeText(this, R.string.update_ok, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.update_error, Toast.LENGTH_LONG).show();
            }
        });
        return true;
    }

    private void input(final Activity activity, final String title, final String currentValue, final int inputType, final Call1<String> onChangeListener) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setTitle(title)
                .setView(R.layout.dialog_input)
                .setPositiveButton(android.R.string.ok, (d, w) -> onChangeListener.call(((EditText)((AlertDialog) d).findViewById(R.id.input)).getText().toString()))
                .setNegativeButton(android.R.string.cancel, (d, w) -> d.dismiss())
                .create();
        dialog.show();
        final EditText editText = dialog.findViewById(R.id.input);
        editText.setText(currentValue);
        editText.setInputType(inputType);
    }

    @Override
    protected void setSearchTerm(final String newSearchTerm) {
        super.setSearchTerm(newSearchTerm);
        ((TextView) findViewById(R.id.searchTerm)).setText(newSearchTerm);
    }

    private int dpToPixel(final float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
