package de.deadlocker8.budgetmaster.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import de.deadlocker8.budgetmaster.logic.Category;
import de.deadlocker8.budgetmaster.logic.Helpers;
import de.deadlocker8.budgetmaster.logic.NormalPayment;
import de.deadlocker8.budgetmaster.logic.Payment;
import de.deadlocker8.budgetmaster.logic.RepeatingPayment;
import de.deadlocker8.budgetmaster.logic.RepeatingPaymentEntry;
import de.deadlocker8.budgetmaster.logic.ServerConnection;
import de.deadlocker8.budgetmaster.ui.cells.ButtonCategoryCell;
import de.deadlocker8.budgetmaster.ui.cells.RepeatingDayCell;
import de.deadlocker8.budgetmaster.ui.cells.SmallCategoryCell;
import fontAwesome.FontIcon;
import fontAwesome.FontIconType;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logger.LogLevel;
import logger.Logger;
import tools.AlertGenerator;
import tools.ConvertTo;

public class NewPaymentController
{
	@FXML private TextField textFieldName;
	@FXML private TextField textFieldAmount;
	@FXML private Button buttonCancel;
	@FXML private Button buttonSave;
	@FXML private ComboBox<Category> comboBoxCategory;
	@FXML private DatePicker datePicker;
	@FXML private DatePicker datePickerEnddate;
	@FXML private Spinner<Integer> spinnerRepeatingPeriod;
	@FXML private ComboBox<Integer> comboBoxRepeatingDay;
	@FXML private CheckBox checkBoxRepeat;
	@FXML private RadioButton radioButtonPeriod;
	@FXML private RadioButton radioButtonDay;
	@FXML private Label labelText1, labelText2, labelText3;

	private Stage stage;
	private Controller controller;
	private PaymentController paymentController;
	private boolean isPayment;
	private boolean edit;
	private Payment payment;
	private ButtonCategoryCell buttonCategoryCell;

	public void init(Stage stage, Controller controller, PaymentController paymentController, boolean isPayment, boolean edit, Payment payment)
	{
		this.stage = stage;
		this.controller = controller;
		this.paymentController = paymentController;
		this.isPayment = isPayment;
		this.edit = edit;
		this.payment = payment;

		FontIcon iconCancel = new FontIcon(FontIconType.TIMES);
		iconCancel.setSize(17);
		iconCancel.setStyle("-fx-text-fill: white");
		buttonCancel.setGraphic(iconCancel);
		FontIcon iconSave = new FontIcon(FontIconType.SAVE);
		iconSave.setSize(17);
		iconSave.setStyle("-fx-text-fill: white");
		buttonSave.setGraphic(iconSave);

		buttonCancel.setStyle("-fx-background-color: #2E79B9; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15;");
		buttonSave.setStyle("-fx-background-color: #2E79B9; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15;");

		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0);
		spinnerRepeatingPeriod.setValueFactory(valueFactory);
		spinnerRepeatingPeriod.setEditable(true);
		spinnerRepeatingPeriod.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if(!newValue)
			{
				spinnerRepeatingPeriod.increment(0); // won't change value, but will commit editor
			}
		});

		comboBoxRepeatingDay.setCellFactory((view) -> {
			return new RepeatingDayCell();
		});
		ArrayList<Integer> days = new ArrayList<>();
		for(int i = 0; i <= 31; i++)
		{
			days.add(i);
		}
		comboBoxRepeatingDay.getItems().addAll(days);
		
		comboBoxCategory.setCellFactory((view) -> {
			return new SmallCategoryCell();
		});
		buttonCategoryCell = new ButtonCategoryCell(Color.WHITE);
		comboBoxCategory.setButtonCell(buttonCategoryCell);
		comboBoxCategory.setStyle("-fx-border-color: #000000; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5;");
		comboBoxCategory.valueProperty().addListener((listener, oldValue, newValue) -> {		
			comboBoxCategory.setStyle("-fx-background-color: " + ConvertTo.toRGBHex(newValue.getColor()) + "; -fx-border-color: #000000; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5;");
			buttonCategoryCell.setColor(newValue.getColor());
		});

		checkBoxRepeat.selectedProperty().addListener((listener, oldValue, newValue) -> {
			toggleRepeatingArea(newValue);
		});

		comboBoxCategory.getItems().clear();
		try
		{
			ServerConnection connection = new ServerConnection(controller.getSettings());
			ArrayList<Category> categories = connection.getCategories();
			if(categories != null)
			{
				comboBoxCategory.getItems().addAll(categories);
			}
		}
		catch(Exception e)
		{
			controller.showConnectionErrorAlert();
			stage.close();
			return;
		}

		final ToggleGroup toggleGroup = new ToggleGroup();
		radioButtonPeriod.setToggleGroup(toggleGroup);
		radioButtonDay.setToggleGroup(toggleGroup);
		radioButtonPeriod.selectedProperty().addListener((listener, oldValue, newValue) -> {
			toggleRadioButtonPeriod(newValue);
		});

		datePickerEnddate.setDayCellFactory((p) -> new DateCell()
		{
			@Override
			public void updateItem(LocalDate ld, boolean bln)
			{
				super.updateItem(ld, bln);

				if(datePicker.getValue() != null && ld.isBefore(datePicker.getValue()))
				{
					setDisable(true);
					setStyle("-fx-background-color: #ffc0cb;");
				}
			}
		});

		if(edit)
		{
			textFieldName.setText(payment.getName());
			textFieldAmount.setText(Helpers.NUMBER_FORMAT.format(Math.abs(payment.getAmount()/100.0)).replace(".", ","));		
			comboBoxCategory.setValue(controller.getCategoryHandler().getCategory(payment.getCategoryID()));
			datePicker.setValue(LocalDate.parse(payment.getDate()));
			
			if(payment instanceof RepeatingPaymentEntry)
			{
				RepeatingPaymentEntry currentPayment = (RepeatingPaymentEntry)payment;
				//repeates every x days
				if(currentPayment.getRepeatInterval() != 0)
				{					
					checkBoxRepeat.setSelected(false);
					radioButtonPeriod.setSelected(true);
					toggleRepeatingArea(true);
					spinnerRepeatingPeriod.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, currentPayment.getRepeatInterval()));
				}
				//repeat every month on day x
				else
				{
					checkBoxRepeat.setSelected(false);
					radioButtonDay.setSelected(true);
					toggleRepeatingArea(true);
					comboBoxRepeatingDay.getSelectionModel().select(currentPayment.getRepeatMonthDay());
				}
				if(currentPayment.getRepeatEndDate() != null)
				{
					datePickerEnddate.setValue(LocalDate.parse(currentPayment.getRepeatEndDate()));
				}
			}	
			else
			{				
				checkBoxRepeat.setSelected(false);
				radioButtonPeriod.setSelected(true);
				toggleRepeatingArea(false);
			}
		}
		else
		{
			comboBoxCategory.getSelectionModel().select(0);
			checkBoxRepeat.setSelected(false);
			radioButtonPeriod.setSelected(true);
			toggleRepeatingArea(false);
		}
	}

	public void save()
	{
		String name = textFieldName.getText();
		if(name == null || name.equals(""))
		{
			AlertGenerator.showAlert(AlertType.WARNING, "Warnung", "", "Das Feld f�r den Namen darf nicht leer sein.", controller.getIcon(), controller.getStage(), null, false);
			return;
		}

		String amountText = textFieldAmount.getText();
		if(!amountText.matches("^-?\\d+(,\\d+)*(\\.\\d+(e\\d+)?)?$"))
		{
			AlertGenerator.showAlert(AlertType.WARNING, "Warnung", "", "Gib eine g�ltige Zahl f�r den Betrag ein.", controller.getIcon(), controller.getStage(), null, false);
			return;
		}

		LocalDate date = datePicker.getValue();
		if(date == null)
		{
			AlertGenerator.showAlert(AlertType.WARNING, "Warnung", "", "Bitte w�hle ein Datum aus.", controller.getIcon(), controller.getStage(), null, false);
			return;
		}

		int amount = 0;
		amount = (int)(Double.parseDouble(amountText.replace(",", ".")) * 100);
		if(isPayment)
		{
			amount = -amount;
		}

		int repeatingInterval = 0;
		int repeatingDay = 0;
		if(checkBoxRepeat.isSelected())
		{
			if(radioButtonPeriod.isSelected())
			{
				repeatingInterval = spinnerRepeatingPeriod.getValue();
			}
			else
			{
				repeatingDay = comboBoxRepeatingDay.getValue();
			}

			if(repeatingInterval == 0 && repeatingDay == 0)
			{
				AlertGenerator.showAlert(AlertType.WARNING, "Warnung", "", "Wenn Wiederholung aktiviert ist d�rfen nicht beide Eingabefelder 0 sein.\n(Zur Deaktivierung der Wiederholung einfach die Checkbox enthaken)", controller.getIcon(), controller.getStage(), null, false);
				return;
			}

			if(datePickerEnddate.getValue() != null && datePickerEnddate.getValue().isBefore(date))
			{
				AlertGenerator.showAlert(AlertType.WARNING, "Warnung", "", "Das Enddatum darf zeitlich nicht vor dem Datum der Zahlung liegen.", controller.getIcon(), controller.getStage(), null, false);
				return;
			}

			if(edit)
			{
				RepeatingPaymentEntry newPayment = new RepeatingPaymentEntry(payment.getID(), ((RepeatingPaymentEntry)payment).getRepeatingPaymentID(), getDateString(date), amount, comboBoxCategory.getValue().getID(), name, repeatingInterval, getDateString(datePickerEnddate.getValue()),
						repeatingDay);
				try
				{
					ServerConnection connection = new ServerConnection(controller.getSettings());
					connection.updateRepeatingPayment(newPayment, payment);
				}
				catch(Exception e)
				{
					Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
					controller.showConnectionErrorAlert();
				}
			}
			else
			{
				RepeatingPayment newPayment = new RepeatingPayment(-1, amount, getDateString(date), comboBoxCategory.getValue().getID(), name, repeatingInterval, getDateString(datePickerEnddate.getValue()), repeatingDay);
				try
				{
					ServerConnection connection = new ServerConnection(controller.getSettings());
					connection.addRepeatingPayment(newPayment);
				}
				catch(Exception e)
				{
					Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
					controller.showConnectionErrorAlert();
				}
			}
		}
		else
		{
			if(edit)
			{
				NormalPayment newPayment = new NormalPayment(payment.getID(), amount, getDateString(date), comboBoxCategory.getValue().getID(), name);
				try
				{
					ServerConnection connection = new ServerConnection(controller.getSettings());
					connection.updateNormalPayment(newPayment, payment);
				}
				catch(Exception e)
				{
					Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
					controller.showConnectionErrorAlert();
				}
			}
			else
			{
				NormalPayment newPayment = new NormalPayment(-1, amount, getDateString(date), comboBoxCategory.getValue().getID(), name);
				try
				{
					ServerConnection connection = new ServerConnection(controller.getSettings());
					connection.addNormalPayment(newPayment);
				}
				catch(Exception e)
				{
					Logger.log(LogLevel.ERROR, Logger.exceptionToString(e));
					controller.showConnectionErrorAlert();
				}
			}
		}

		stage.close();
		paymentController.getController().refresh();
	}

	public void cancel()
	{
		stage.close();
	}

	private void toggleRepeatingArea(boolean selected)
	{
		if(selected)
		{
			if(radioButtonPeriod.isSelected())
			{
				spinnerRepeatingPeriod.setDisable(false);
				comboBoxRepeatingDay.setDisable(true);
			}
			else
			{
				spinnerRepeatingPeriod.setDisable(true);
				comboBoxRepeatingDay.setDisable(false);
			}
		}
		else
		{
			spinnerRepeatingPeriod.setDisable(!selected);
			comboBoxRepeatingDay.setDisable(!selected);
		}
		datePickerEnddate.setDisable(!selected);
		radioButtonPeriod.setDisable(!selected);
		radioButtonDay.setDisable(!selected);
		labelText1.setDisable(!selected);
		labelText2.setDisable(!selected);
		labelText3.setDisable(!selected);
	}

	private void toggleRadioButtonPeriod(boolean selected)
	{
		spinnerRepeatingPeriod.setDisable(!selected);
		labelText1.setDisable(!selected);
		labelText2.setDisable(!selected);
		comboBoxRepeatingDay.setDisable(selected);
		labelText3.setDisable(selected);
	}

	private String getDateString(LocalDate date)
	{
		if(date == null)
		{
			return "";
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return date.format(formatter);
	}
}