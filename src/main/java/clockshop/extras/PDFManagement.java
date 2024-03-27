package clockshop.extras;


import clockshop.grandfatherclock.GFCCartItem;
import clockshop.maintenance.MaintenanceContract;
import clockshop.order.ShopOrderManagement;
import clockshop.repair.Repair;
import com.google.zxing.WriterException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.javamoney.moneta.Money;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;

@Service
public class PDFManagement {

	private static final Logger logger = LoggerFactory.getLogger(PDFManagement.class);

	private static final Font HEADER = FontFactory.getFont(FontFactory.TIMES_ROMAN, 16, Font.UNDERLINE, BaseColor.BLACK);
	private static final Font FONT = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
	private static final Font SMALLFONT = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, BaseColor.BLACK);
	private static final String CLOCKSHOP = "An Uhren Gmbh.";
	private static final String ADDRESS = "Uhrenladenstr. 86, 01187 Dresden";
	private static final String BANK =
		"Bankverbindung: Sparkasse Dresden\nIBAN: DE025 00105171768232559\nBIC/SWIFT-Code: 50010517\n";
	private static final double HOUR_RATE = 21;
	private static final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

	private final ShopOrderManagement shopOrderManagement;

	public PDFManagement(ShopOrderManagement shopOrderManagement) {
		this.shopOrderManagement = shopOrderManagement;
	}

	/**
	 * Generates a PDF document for an order confirmation, including details about the order,
	 * customer information, and payment instructions. The document is finalized and opened for viewing.
	 *
	 * @param order      The Order object representing the customer's order.
	 * @param forename   The forename of the customer.
	 * @param name       The surname of the customer.
	 * @param address    The address of the customer.
	 * @param telephone  The telephone number of the customer.
	 * @throws DocumentException If there is an error related to the PDF document.
	 * @throws IOException       If there is an error reading or writing the QR code or the document.
	 * @throws WriterException   If there is an error generating the QR code.
	 */
	// defaultOrder
	public static void pdfOrderFinished(Order order,
										String forename,
										String name,
										String address,
										String telephone) throws DocumentException, IOException, WriterException {
		Document document = createDocument();
		LocalDateTime date = order.getDateCreated();
		String f_date = date.getDayOfMonth() + "." + date.getMonthValue()
			+ "." + date.getYear();
		Paragraph header = new Paragraph("Bestellbestätigung", HEADER);
		String orderLines = "";

		for(OrderLine orderLine: order.getOrderLines()){
			orderLines += orderLine.getQuantity().toString() + " x "
				+ orderLine.getProductName()
				+ " - " + orderLine.getPrice() + " \n";
		}
		Paragraph plainText = new Paragraph
			("\n" +
				"Datum: " + f_date + " \n" +
				"\n" +
				"Bestellnummer: " + order.getId() + "\n" +
				"\n" +
				"Kundeninformation: \n" +
				forename + "\n" +
				name + "\n" +
				address + "\n" +
				telephone + "\n" +
				"\n" +
				"Artikel: \n" + orderLines +
				"\n" +
				"Insgesamt: " + order.getTotal() + "(Inkl. Mwst.)" + "\n" +
				"Zahlungsbedingungen:" + "\n" +
				"Bitte begleichen Sie den Gesamtbetrag innerhalb von 14 Tagen auf das folgende Konto:\n" +
				"\n" +
				"\n" +
				BANK + "\n" +
				"Die Zahlung ist innerhalb der genannten Frist zu begleichen.\n" +
				"Bei Fragen zur Rechnung oder Zahlungsmodalitäten stehen wir Ihnen gerne zur Verfügung.\n" +
				"\n" +
				"\n" +
				CLOCKSHOP + "\n" +
				ADDRESS + "\n", FONT);
		document.add(header);
		document.add(plainText);
		finishPDF(document);
	}

	/**
	 * Generates a PDF document for an invoice at the end of a repair service.
	 * The document includes details about the repair, customer information, services performed,
	 * cost breakdown, and payment instructions. The document is finalized and opened for viewing.
	 *
	 * @param repair The Repair object representing the completed repair service.
	 * @throws DocumentException If there is an error related to the PDF document.
	 * @throws IOException       If there is an error reading or writing the PDF document.
	 * @throws WriterException   If there is an error generating the QR code.
	 */
	//Repair
	public void pdfEndRepair(Repair repair) throws IOException, DocumentException, WriterException {
		//clockshop
		//Reparaturitem, Materialauflistung
		Order invoice = shopOrderManagement.getOrder(repair.getOrderIdentifier());
		Document document = createDocument();
		Paragraph header = new Paragraph("Rechnung", HEADER);
		Paragraph plainText = new Paragraph
			("\nRechnungsnummer: " + invoice.getId()
				+ "\n" +
				"Datum: " + repair.getFormattedDateTime(LocalDateTime.now()) + "\n" +
				"\n" +
				"Kunde:\n" +
				repair.getCustomerName() + "\n" +
				repair.getCustomerAddress() + "\n" +
				repair.getTelephoneNumber() + "\n" +
				repair.getEmail() + "\n" +
				"\n" +
				"Leistungen:\n" +
				"Uhrreparatur: " + repair.getRepairType() + "\n" +
				"\n" +
				"Kostenübersicht:\n" +
				"Reparaturkosten: " + repair.getCostEstimate() + "€ \n" +
				"Materialkosten: " + invoice.getOrderLines().getTotal().getNumber().intValue() + "€ \n" +
				"\n" +
				"Gesamtbetrag (inkl. MwSt.): " + repair.getPrice().getNumber().intValue() + "€\n" +
				"\n" +
				"Zahlungsbedingungen:" +
				" Bitte begleichen Sie den Gesamtbetrag innerhalb von 14 Tagen auf das folgende Konto:\n" +
				"\n" +
				BANK +
				"Rechnungsnummer: " + invoice.getId() + "\n" +
				"\n" +
				"Hinweise:\n" +
				"\n" +
				"Die Zahlung ist innerhalb der genannten Frist zu begleichen.\n" +
				"Bei Fragen zur Rechnung oder Zahlungsmodalitäten stehen wir Ihnen gerne zur Verfügung.\n" +
				"Bitte beachten Sie, dass bis zur vollständigen Begleichung des Rechnungsbetrags das Eigentum" +
				" an der reparierten Uhr beim Uhrenladen verbleibt.\n" +
				"Vielen Dank für Ihr Vertrauen in unsere Dienstleistungen." +
				" Wir hoffen, Ihre Erwartungen erfüllt zu haben, und freuen uns darauf, Sie bald wiederzusehen.\n" +
				"\n" +
				"Mit freundlichen Grüßen,\n" +
				"\n" +
				CLOCKSHOP + "\n" +
				ADDRESS + "\n", FONT);
		document.add(header);
		document.add(plainText);
		finishPDF(document);
	}

	/**
	 * Generates a PDF document for an order confirmation at the beginning of a repair service.
	 * The document includes customer information, repair details, cost estimate, and additional information.
	 * The document is finalized and opened for viewing.
	 *
	 * @param repair The Repair object representing the repair service initiation.
	 * @throws DocumentException If there is an error related to the PDF document.
	 * @throws IOException       If there is an error reading or writing the PDF document.
	 * @throws WriterException   If there is an error generating the QR code.
	 */
	public void pdfStartRepair(Repair repair) throws DocumentException, IOException, WriterException {
		//Feld Unterschrift
		//clockshop
		//create the document
		Document document = createDocument();
		Paragraph header = new Paragraph("Auftragsbestätigung", HEADER);

		Paragraph plainText = new Paragraph(
			"\n\nSehr geehrte/r " + repair.getCustomerName() + ",\n" +
				"\n" +
				"Wir möchten Ihnen hiermit bestätigen," +
				" dass wir Ihren Auftrag zur Reparatur Ihrer Uhr erhalten haben." +
				" Die Details der Reparatur sind wie folgt:\n" +
				"\n" +
				"Kunde: " + repair.getCustomerName() + "\n" +
				"Adresse: " + repair.getCustomerAddress() + "\n" +
				"Telefonnummer: " + repair.getTelephoneNumber() + "\n" +
				"Email: " + repair.getEmail() + "\n" +
				"Kostenvoranschlag: " + repair.getCostEstimate() + "€\n" +
				"Reparaturart: " + repair.getRepairType() + "\n" +
				"Vertragsnummer: " + repair.getId() + "\n" +
				"\n" +
				"Beschreibung des Problems: " + repair.getDescription() + "\n" +
				"Die voraussichtliche Dauer der Reparatur beträgt 2 Wochen." +
				" Bitte beachten Sie, dass diese Schätzung von der Art" +
				" der notwendigen Reparatur abhängt und Änderungen unterliegen kann.\n" +
				"\n" +
				"Die Kosten für die Reparatur belaufen sich voraussichtlich auf "
				+ repair.getCostEstimate() +
				"€. Sollten während der Reparatur weitere Probleme oder zusätzliche Kosten auftreten," +
				" werden wir Sie umgehend informieren, bevor wir weitere Maßnahmen ergreifen.\n" +
				"\n" +
				"Bitte bewahren Sie diese Auftragsbestätigung für eventuelle Rückfragen auf." +
				" Wir werden uns mit Ihnen in Verbindung setzen, sobald die Reparatur abgeschlossen ist," +
				" damit Sie Ihre Uhr wieder in einwandfreiem Zustand abholen können.\n" +
				"\n" +
				"Wir schätzen Ihr Vertrauen in unsere Dienstleistungen und" +
				" stehen Ihnen für jegliche Fragen gerne zur Verfügung.\n" +
				"\n" +
				"Mit freundlichen Grüßen,\n" +
				"\n" +
				CLOCKSHOP + "\n" +
				ADDRESS + "\n", FONT);
		document.add(header);
		document.add(plainText);
		finishPDF(document);
	}

	/**
	 * Generates a PDF document for a maintenance contract confirmation.
	 * The document includes details about the contract, customer information, service scope,
	 * cost breakdown, and payment instructions. The document is finalized and opened for viewing.
	 *
	 * @param contract The MaintenanceContract object representing the maintenance contract details.
	 * @throws DocumentException If there is an error related to the PDF document.
	 * @throws IOException       If there is an error reading or writing the PDF document.
	 * @throws WriterException   If there is an error generating the QR code.
	 */
	public void pdfMaintenanceContract(MaintenanceContract contract)
		throws DocumentException, IOException, WriterException {
		Document document = createDocument();
		Paragraph header = new Paragraph("Auftragsbestätigung", HEADER);
		LocalDateTime date = LocalDateTime.now();
		String f_date = date.getDayOfMonth() + "." + date.getMonthValue()
			+ "." + date.getYear();
		Paragraph plainText = new Paragraph(
			"Datum: " + f_date + "\n" +
				"\n" +
				"Kundennummer: " + contract.getId() + "\n" +
				"\n" +
				"Sehr geehrte Damen und Herren,\n" +
				"\n" +
				"hiermit bestätigen wir den Auftrag zur Wartung der Turm- und" +
				" Gebäudeuhren gemäß unserer vorherigen Vereinbarung." +
				" Wir freuen uns, unseren Wartungsservice für Ihre wertvollen Zeitmessgeräte bereitzustellen.\n" +
				"\n" +
				"Auftragsdetails:\n" +
				"\n" +
				"Kunde: " + contract.getCompany() + "\n" +
				"Adresse: " + contract.getAddress() +"\n" +
				"Kontaktperson: " + contract.getContactPerson() +"\n" +
				"\n" +
				"Dienstleistungsumfang:\n" +
				"\n" +
				"Wartung Turmuhren: " + contract.getTowerQuantity() + "\n" +
				"Wartung Gebäudeuhren: " + contract.getBuildingQuantity() + "\n" +
				"Kostenübersicht:\n" +
				"Gesamtkosten: " + contract.getPrice().getNumber().intValue() + "€\n" +
				"\n" +
				"Bitte entrichten Sie die Zahlung monatlich unbar zum " + date.getDayOfMonth() +
				". eines jeden Monats auf das nachstehende Konto. \n \n" +
				BANK +
				"\n" +
				"Bitte bestätigen Sie den Erhalt dieser Auftragsbestätigung und den vereinbarten Wartungstermin," +
				" indem Sie uns eine kurze Rückmeldung senden oder uns telefonisch kontaktieren.\n" +
				"\n" +
				"Für Rückfragen stehen wir Ihnen jederzeit gerne zur Verfügung." +
				" Wir bedanken uns für Ihr Vertrauen in unsere Dienstleistungen und" +
				" freuen uns auf eine erfolgreiche Zusammenarbeit.\n" +
				"\n" +
				"Mit freundlichen Grüßen,\n" +
				"\n" +
				CLOCKSHOP + "\n" +
				ADDRESS, FONT);
		document.add(header);
		document.add(plainText);
		finishPDF(document);
	}

	/**
	 * Generates a PDF document for a GFC (Generic Furniture Company) order confirmation.
	 * The document includes details about the order, customer information, item description,
	 * quantity, price breakdown, and payment instructions. The document is finalized and opened for viewing.
	 *
	 * @param gfcCartItem       The GFCCartItem object representing the ordered item details.
	 * @param customerName      The name of the customer placing the order.
	 * @param customerAddress   The address of the customer placing the order.
	 * @param customerTelephone The telephone number of the customer placing the order.
	 * @throws DocumentException If there is an error related to the PDF document.
	 * @throws IOException       If there is an error reading or writing the PDF document.
	 * @throws WriterException   If there is an error generating the QR code.
	 */
	public static void pdfGFCOrderConfirmation(GFCCartItem gfcCartItem,
											   String customerName,
											   String customerAddress,
											   String customerTelephone) throws DocumentException, IOException, WriterException {
		Document document = createDocument();
		Paragraph header = new Paragraph("Bestätigung der Bestellung", HEADER);
		LocalDateTime date = LocalDateTime.now();
		String f_date = date.getDayOfMonth() + "." + date.getMonthValue()
			+ "." + date.getYear();
		Paragraph plainText = new Paragraph
			("Datum:" + f_date + "\n" +
				"Kundeninformationen:\n" +
				"\n" +
				customerName + "\n" +
				customerAddress + "\n" +
				customerTelephone + "\n" +
				"\n" +
				"Sehr geehrte/r " + customerName + ", \n \n" +
				"wir bestätigen hiermit den Eingang Ihrer Bestellung für die Standuhr Modell:" +
				"'" + gfcCartItem.getName() + "' von " + gfcCartItem.getCompanyName() + "\n" + "\n" +
				"Beschreibung der Bestellung: \n \n" +
				"Artikel: Standuhr Model: '" + gfcCartItem.getName() + "' \n" +
				"Hersteller: " + gfcCartItem.getCompanyName() + " \n" +
				"Menge: " + gfcCartItem.getQuantity().getAmount().intValue() + "x" + gfcCartItem.getPrice() + "\n" +
				"		+ EUR 50 Provision" + "\n" +
				"Insgesamt: " +
				gfcCartItem.getPrice().multiply(
					gfcCartItem.getQuantity().getAmount()).add(
						Money.of(50,"EUR")) + "(inkl. Mwst.) \n \n" +
				"Bitte überweisen Sie den Gesamtbetrag innerhalb von 14 Tagen auf das folgende Bankkonto:" + "\n" +
				BANK + "\n" +
				"Wir bedanken uns für Ihre Bestellung!" + "\n" +
				"Mit freundlichen Grüßen," + "\n" +
				CLOCKSHOP + "\n" +
				ADDRESS,
				FONT);
		document.add(header);
		document.add(plainText);
		finishPDF(document);

	}

	// Stuff that's used in all Cases

	/**
	 * Adds a QR code to the given PDF document, representing the contact email address.
	 * The QR code is generated using the QrCodeService class.
	 *
	 * @param document The PDF document to which the QR code and related information will be added.
	 * @return The updated PDF document with the added QR code.
	 * @throws DocumentException If there is an error related to the PDF document.
	 * @throws IOException If there is an error reading or writing the QR code or the document.
	 * @throws WriterException If there is an error generating the QR code.
	 */

	public static Document addQR(Document document) throws DocumentException, IOException, WriterException {
		// QR CODE
		Image qrCode = QrCodeService.generateQrCode("uhrenladenswt@gmail.com");
		Paragraph qr = new Paragraph("Contact us (scan QR-Code below)", FONT);
		qrCode.setAlignment(Element.ALIGN_LEFT);
		qr.setAlignment(Element.ALIGN_LEFT);
		document.add(qrCode);
		document.add(qr);
		document.close();

		return document;
	}

	/**
	 * Creates and opens a new PDF document.
	 *
	 * @return The newly created PDF document.
	 * @throws DocumentException If there is an error creating the PDF document.
	 */
	public static Document createDocument() throws DocumentException {
		Document document = new Document();
		PdfWriter.getInstance(document, byteArrayOutputStream);
		document.open();
		return document;
	}
	/**
	 * Finalizes the PDF document, adds a QR code, and opens the document for viewing.
	 *
	 * @param document The PDF document to be finalized.
	 * @throws DocumentException If there is an error related to the PDF document.
	 * @throws IOException If there is an error reading or writing the QR code or the document.
	 * @throws WriterException If there is an error generating the QR code.
	 */

	public static void finishPDF(Document document) throws DocumentException, IOException, WriterException {
		// Stuff
		document = addQR(document);
		File tempFile = File.createTempFile("temp", ".pdf");
		try (FileOutputStream fos = new FileOutputStream(tempFile)) {
			fos.write(byteArrayOutputStream.toByteArray());
		} catch (IOException e) {
			throw new FileNotFoundException(e.getMessage());
		}

		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + tempFile.getAbsolutePath());
		} else if (os.contains("nix") || os.contains("nux")) {
			Runtime.getRuntime().exec("xdg-open " + tempFile.getAbsolutePath());
		} else if (os.contains("mac")) {
			Runtime.getRuntime().exec("open " + tempFile.getAbsolutePath());
		} else {
			logger.warn("Unsupported operating system");
		}
	}


}