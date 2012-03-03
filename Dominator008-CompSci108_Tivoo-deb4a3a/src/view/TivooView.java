package view;

import java.io.File;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;


import controller.TivooController;

import model.TivooModel;

@SuppressWarnings("serial")
public class TivooView extends JPanel {
	
	public static final Dimension SIZE = new Dimension(800, 600);
	public static final String BLANK = " ";

	
	private JTextField startTime;
	private JTextField endTime;
	private JTextField keyWord1;
	private JTextField keyWord2;
	private JTextField keyWord3;

	private JTextField destination;
	private JTextField detailDest;
	private TivooController myController;

	private JButton myAddButton;
	private DefaultComboBoxModel myWriters;
	private JComboBox myWritersDisplay;

	protected TivooModel myModel;

	
	public TivooView(TivooModel model) {
		myModel = model;
		
		setLayout(new BorderLayout());
		
		add(makeInputPanel(), BorderLayout.NORTH);
		add(filterPanel(), BorderLayout.SOUTH);
	}

	public void giveController(TivooController controller) {
		myController = controller;
	}

	public File getFile() {
		JFileChooser fc = new JFileChooser();
		File file = null;
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();

		}
		return file;
	}

	private JComponent filterPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(makeFilterPanel1(), BorderLayout.NORTH);
		panel.add(makeFilterPanel2(), BorderLayout.SOUTH);
		return panel;
	}

	private JComponent makeInputPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(makeDestinationPanel(), BorderLayout.NORTH);
		panel.add(makeAddPanel(), BorderLayout.LINE_START);
		panel.add(filterLabel(), BorderLayout.SOUTH);
		return panel;
	}

	private JComponent filterLabel() {
		JPanel panel = new JPanel();
		JLabel label1 = new JLabel("Optional filters: ");
		panel.add(label1);
		return panel;
	}

	private JComponent makeDestinationPanel() {
		JPanel panel = new JPanel();

		JLabel label1 = new JLabel("Destination File");
		panel.add(label1);

		destination = new JTextField(10);

		panel.add(destination);

		JLabel label2 = new JLabel("Detail Destination Folder");
		panel.add(label2);

		detailDest = new JTextField(10);
		panel.add(detailDest);

		myWriters = new DefaultComboBoxModel();
		myWriters.addElement("Daily Calendar");
		myWriters.addElement("Weekly Calendar");
		myWriters.addElement("Monthly Calendar");
		myWriters.addElement("Conflicting Events");
		myWritersDisplay = new JComboBox(myWriters);
		panel.add(myWritersDisplay);

		return panel;
	}

	private JComponent makeFilterPanel1() {
		JPanel panel = new JPanel();

		JLabel starttime = new JLabel("Start Date (MMddYYYY)");
		panel.add(starttime);
		startTime = new JTextField(10);
		panel.add(startTime);

		JLabel endtime = new JLabel("End Date (MMddYYYY)");
		panel.add(endtime);
		endTime = new JTextField(10);
		panel.add(endTime);

		return panel;
	}

	private JComponent makeFilterPanel2() {
		JPanel panel = new JPanel();

		JLabel keywords = new JLabel("Keywords");
		panel.add(keywords);
		keyWord1 = new JTextField(10);
		panel.add(keyWord1);
		keyWord2 = new JTextField(10);
		panel.add(keyWord2);
		keyWord3 = new JTextField(10);
		panel.add(keyWord3);

		JButton goButton = new JButton("Go");
		goButton.addActionListener(new GoAction());
		panel.add(goButton);

		return panel;
	}

	private JComponent makeAddPanel() {
		JPanel panel = new JPanel();

		myAddButton = new JButton("Add Another File");
		myAddButton.addActionListener(new AddFileAction());
		panel.add(myAddButton);

		return panel;
	}

	private class GoAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			myController.go(destination.getText(), detailDest.getText(),
					myWriters.getSelectedItem().toString(), startTime.getText(), endTime.getText(), 
					keyWord1.getText(), keyWord2.getText(), keyWord3.getText());
		}
	}

	private class AddFileAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			File f = getFile();
			myController.read(f);
		}
	}

}
