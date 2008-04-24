package prefuse.util.io;

import java.awt.*;
import javax.swing.*;
import java.awt.print.*;

public class PrintLib implements Printable {

	private JComponent componentToBePrinted;

	public static void printComponent(JComponent c) {
		new PrintLib(c).print();
	}

	public PrintLib(JComponent componentToBePrinted) {
		this.componentToBePrinted = componentToBePrinted;
	}

	public void print() {
		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setPrintable(this);
		if (printJob.printDialog()) {
			try {
				printJob.print();
			} catch (PrinterException pe) {
				System.out.println("Error printing: " + pe);
			}
		}
	}

	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
		if (pageIndex > 0) {
			return (NO_SUCH_PAGE);
		} else {
			Graphics2D g2d = (Graphics2D) g;
			g2d.translate(pageFormat.getImageableX(), pageFormat
					.getImageableY());
			componentToBePrinted.paint(g2d);
			return (PAGE_EXISTS);
		}
	}

}
