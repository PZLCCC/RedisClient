package com.cxy.redisclient.presentation.zset;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.ModifyEvent;

import com.cxy.redisclient.dto.ZSetInfo;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;

public class NewZSetDialog extends Dialog {

	protected Object result;
	protected Shell shlNewSortedSet;
	private String server;
	private int db;
	private String key;
	private Text text;
	private Table table;
	private Button btnDelete;
	private Button btnOk;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public NewZSetDialog(Shell parent, int style, String server, int db,
			String key) {
		super(parent, style);
		setText("SWT Dialog");
		this.server = server;
		this.db = db;
		this.key = key == null ? "" : key;
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlNewSortedSet.open();
		shlNewSortedSet.layout();
		Display display = getParent().getDisplay();
		while (!shlNewSortedSet.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlNewSortedSet = new Shell(getParent(), getStyle());
		shlNewSortedSet.setSize(479, 373);
		shlNewSortedSet.setText("New Sorted Set");

		Rectangle screenSize = shlNewSortedSet.getParent().getBounds();
		Rectangle shellSize = shlNewSortedSet.getBounds();
		shlNewSortedSet.setLocation(screenSize.x + screenSize.width / 2
				- shellSize.width / 2, screenSize.y + screenSize.height / 2
				- shellSize.height / 2);
		shlNewSortedSet.setLayout(new GridLayout(1, false));

		TabFolder tabFolder = new TabFolder(shlNewSortedSet, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		tabFolder.setSize(414, 241);

		TabItem tbtmList = new TabItem(tabFolder, SWT.NONE);
		tbtmList.setText("Sorted Set");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmList.setControl(composite);
		composite.setLayout(new GridLayout(4, true));

		Label label = new Label(composite, SWT.NONE);
		label.setText("Server");

		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setText(server);

		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setText("Database");

		Label label_3 = new Label(composite, SWT.NONE);
		label_3.setText(String.valueOf(db));

		Label lblKey = new Label(composite, SWT.NONE);
		lblKey.setText("Key");

		text = new Text(composite, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		text.setText(key);
		text.selectAll();
		text.setFocus();
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String newKey = text.getText() == null ? "" : text.getText();
				if (newKey.equals(key))
					btnOk.setEnabled(false);
				else
					btnOk.setEnabled(true);
			}
		});

		Group grpValues = new Group(composite, SWT.NONE);
		grpValues.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4,
				1));
		grpValues.setText("Values");
		grpValues.setLayout(new GridLayout(4, false));

		table = new Table(grpValues, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.MULTI);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 3));
		table.setHeaderVisible(true);
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableItemSelected();
			}
		});
		table.setLinesVisible(true);

		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(88);
		tblclmnNewColumn.setText("Score");

		TableColumn tblclmnMember = new TableColumn(table, SWT.NONE);
		tblclmnMember.setWidth(164);
		tblclmnMember.setText("Member");

		Button btnAdd = new Button(grpValues, SWT.NONE);
		btnAdd.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog inputDialog = new InputDialog(
						shlNewSortedSet,
						"Input values",
						"Values(multiple values are separated by ; score and member are separated by ,)",
						"", null);
				if (inputDialog.open() == InputDialog.OK) {
					String values = inputDialog.getValue();
					String[] zsetValues = values.split(";");
					for (String value : zsetValues) {
						TableItem item = new TableItem(table, SWT.NONE);
						String[] zset = value.split(",");
						item.setText(zset);
					}
				}
			}
		});
		btnAdd.setText("Add");

		btnDelete = new Button(grpValues, SWT.NONE);
		btnDelete.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false,
				1, 1));
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = table.getSelection();
				for (TableItem item : items) {
					item.dispose();
				}
				tableItemSelected();
			}
		});
		btnDelete.setEnabled(false);
		btnDelete.setText("Delete");
		new Label(grpValues, SWT.NONE);
		

		Composite composite_1 = new Composite(shlNewSortedSet, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false,
				false, 1, 1));

		btnOk = new Button(composite_1, SWT.NONE);
		btnOk.setEnabled(false);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = table.getItems();
				String key = text.getText();
				Map<String, Double> values = new HashMap<String, Double>();

				if (key.length() == 0 || items.length == 0)
					MessageDialog.openError(shlNewSortedSet, "error",
							"please input sorted set information!");
				else {
					for (TableItem item : items) {
						values.put(item.getText(1),
								Double.valueOf(item.getText(0)));
					}
					result = new ZSetInfo(key, values);
					shlNewSortedSet.dispose();
				}

			}
		});
		btnOk.setText("OK");

		Button btnCancel = new Button(composite_1, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlNewSortedSet.dispose();
			}
		});
		btnCancel.setText("Cancel");

	}

	protected void tableItemSelected() {
		TableItem[] items = table.getSelection();
		if (items.length == 1) {
			btnDelete.setEnabled(true);
		} else if (items.length > 1) {
			btnDelete.setEnabled(true);
		} else {
			btnDelete.setEnabled(false);
		}
	}

}
