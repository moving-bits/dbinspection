package net.movingbits.testapplication;

import net.movingbits.dbinspection.DBInspectionToolkit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.util.Arrays;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.apache.commons.lang3.StringUtils;

/**
 * Sample application for DBInspectionToolkit
 * (c) 2024-2025 moving-bits (<a href="https://github.com/moving-bits">moving-bits</a>)
 */

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DBInspectionToolkit toolkit;
    private static String titleSelectTable;

    @Override
    public void onCreate(final @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dbinspection_activity);
        titleSelectTable = getString(R.string.title_select_table);
        toolkit = new DBInspectionToolkit();
        toolkit.init(this, DataStore.getDatabase(this), titleSelectTable, 10);
        toolkit.prepareBlankTable(R.id.tableView);
        toolkit.setDimensions(dpToPixel(10), dpToPixel(10), dpToPixel(40));

        // initialize table selector
        final AppCompatSpinner spinner = findViewById(R.id.tableSpinner);
        spinner.setOnItemSelectedListener(this);
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, toolkit.getTablenames());
        spinner.setAdapter(spinnerAdapter);

        // prepare UI elements
        findViewById(R.id.tableButtonBack).setEnabled(false);
        findViewById(R.id.tableButtonBack).setOnClickListener(v -> pagination(toolkit.getOffset() - toolkit.getItemsPerPage()));
        findViewById(R.id.tableButtonBack).setOnLongClickListener(v -> pagination(toolkit.getOffset() - 5 * toolkit.getItemsPerPage()));
        final View.OnClickListener editSearch = v -> input(MainActivity.this, getString(R.string.title_search), toolkit.getSearchTerm(), InputType.TYPE_CLASS_TEXT,
                getString(R.string.title_columnSelection), toolkit.getAvailableSearchColumns(), toolkit.getSearchColumnSelection(),
                (n, selection) -> {
            final String newSearchTerm = n.trim();
            if (!StringUtils.equals(newSearchTerm, toolkit.getSearchTerm()) || !Arrays.equals(toolkit.getSearchColumnSelection(), selection)) {
                setSearchTerm(newSearchTerm, selection);
                updateTableData(null);
            }
        });
        findViewById(R.id.tableButtonSearch).setEnabled(false);
        findViewById(R.id.tableButtonSearch).setOnClickListener(editSearch);
        findViewById(R.id.searchTerm).setOnClickListener(editSearch);

        findViewById(R.id.tableButtonForward).setEnabled(false);
        findViewById(R.id.tableButtonForward).setOnClickListener(v -> pagination(toolkit.getOffset() + toolkit.getItemsPerPage()));
        findViewById(R.id.tableButtonForward).setOnLongClickListener(v -> pagination(toolkit.getOffset() + 5 * toolkit.getItemsPerPage()));

        toolkit.setOnColumnHeaderLongClickListener((columnInfo -> {
            AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.columnproperties_title)
                    .setMessage(String.format(getString(R.string.columnproperties_message), columnInfo.name, columnInfo.type, columnInfo.storageClass))
                    .create();
            dialog.show();
            return true;
        }));
        toolkit.setOnFieldLongClickListener((columnInfo, row, inputType, currentValue, isPartOfPrimaryKey) -> {
            if (isPartOfPrimaryKey) {
                Toast.makeText(this, String.format(getString(R.string.error_pkfield), columnInfo.name), Toast.LENGTH_SHORT).show();
                return true;
            }
            input(this, String.format(getString(R.string.title_edit), columnInfo.name, row), currentValue, inputType, newValue -> {
                if (toolkit.persistData(row, columnInfo.name, newValue)) {
                    Toast.makeText(this, R.string.update_ok, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.update_error, Toast.LENGTH_LONG).show();
                }
            });
            return true;
        });
        toolkit.setUpdateTableDataHandler(this::updateTableData);

        initEdgeToEdge();
    }

    private boolean pagination(final int newOffset) {
        toolkit.setOffset(Math.max(0, newOffset));
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

    protected boolean updateTableData(@Nullable final String resetToTable) {
        final boolean moreDataAvailable = toolkit.updateTableDataDefault(resetToTable);
        findViewById(R.id.tableButtonBack).setEnabled(toolkit.getOffset() > 0);
        findViewById(R.id.tableButtonSearch).setEnabled(true);
        findViewById(R.id.tableButtonForward).setEnabled(moreDataAvailable);
        return moreDataAvailable;
    }

    @Override
    public void onNothingSelected(final AdapterView<?> parent) {
        Toast.makeText(parent.getContext(), R.string.error_no_table_selected, Toast.LENGTH_LONG).show();
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

    private void input(final Activity activity, final String title, final String currentValue, final int inputType,
                       final String title2, final CharSequence[] items, final boolean[] selection,
                       final Call2<String, boolean[]> onChangeListener) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setTitle(title)
                .setView(R.layout.dialog_input)
                .setPositiveButton(android.R.string.ok, (d, w) -> {
                    final String newSearchTerm = ((EditText)((AlertDialog) d).findViewById(R.id.input)).getText().toString();
                    if (StringUtils.isBlank(newSearchTerm)) {
                        // skip column selection for empty search term
                        onChangeListener.call(newSearchTerm, toolkit.getSearchColumnSelection());
                    } else {
                        final boolean[] newSelection = Arrays.copyOf(selection, items.length);
                        new MaterialAlertDialogBuilder(activity)
                                .setTitle(title2)
                                .setMultiChoiceItems(items, selection, (dialog1, which, isChecked) -> {
                                    newSelection[which] = isChecked;
                                })
                                .setPositiveButton(android.R.string.ok, (d2, w2) -> onChangeListener.call(newSearchTerm, newSelection))
                                .show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, (d, w) -> d.dismiss())
                .create();
        dialog.show();
        final EditText editText = dialog.findViewById(R.id.input);
        editText.setText(currentValue);
        editText.setInputType(inputType);
    }

    protected void setSearchTerm(final String newSearchTerm, final boolean[] newSearchColumnSelection) {
        toolkit.setSearchTerm(newSearchTerm, newSearchColumnSelection);
        ((TextView) findViewById(R.id.searchTerm)).setText(newSearchTerm);
    }

    private int dpToPixel(final float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    private void initEdgeToEdge() {
        final Window currentWindow = getWindow();
        //enable edge-to-edge downward-compatible
        WindowCompat.enableEdgeToEdge(currentWindow);
        //set window behaviour
        final WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(currentWindow, currentWindow.getDecorView());
        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        //apply edge2edge to activity content view given by activityContentId
        ViewCompat.setOnApplyWindowInsetsListener(currentWindow.getDecorView(), (v, windowInsets) -> {
            final View activityContent = v.findViewById(R.id.activity_content);
            if (activityContent == null) {
                Log.w("edge2edge", "activityContent not found in " + this);
            } else {
                TypedValue tv = new TypedValue();
                if (getTheme().resolveAttribute(androidx.appcompat.R.attr.actionBarSize, tv, true)) {
                    int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
                    // calculate and set the activity_content's insets
                    final Insets innerPadding = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars() | WindowInsetsCompat.Type.displayCutout() | WindowInsetsCompat.Type.ime());
                    activityContent.setPadding(innerPadding.left, innerPadding.top + actionBarHeight, innerPadding.right, innerPadding.bottom);
                }
            }
            return windowInsets;
        });
    }
}
