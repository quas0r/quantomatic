package quanto.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.SortedSet;
import java.util.Stack;

import javax.swing.*;


public class QuantoConsole extends JPanel {
	private static final long serialVersionUID = -5833674157230451213L;
	public PrintStream out;
	public QuantoCore qcore;
	private JTextField input;
	private JTextArea output;
	private Stack<String> history;
	private int hpointer;
	private String prompt;
	
	class QuantoConsoleOutputStream extends OutputStream {
		JTextArea textArea;
		public QuantoConsoleOutputStream(JTextArea textArea) {
			this.textArea = textArea;
		}
		@Override
		public void write(int b) throws IOException {
			textArea.append(String.valueOf((char)b));
			textArea.setCaretPosition(textArea.getDocument().getLength()-1);
		}
		
	}
	
	public QuantoConsole() {
        this.setLayout(new BorderLayout());
        history = new Stack<String>();
        input = new JTextField();
        input.setFocusTraversalKeysEnabled(false);
        output = new JTextArea();
        //output.setFocusable(false);
        out = new PrintStream(new QuantoConsoleOutputStream(output));
		qcore = new QuantoCore(out);
		
		// print and save the default prompt
		prompt = qcore.receive();
		out.print(prompt);
		
		input.addKeyListener(new KeyAdapter () {
			public void keyReleased(KeyEvent e) {
				JTextField tf = (JTextField)e.getSource();
				String text;
				SortedSet<String> compl;
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					if (hpointer > 0) {
						tf.setText(history.get(--hpointer));
					}
					break;
				case KeyEvent.VK_DOWN:
					if (hpointer < history.size()-1) {
						tf.setText(history.get(++hpointer));
					}
					break;
				case KeyEvent.VK_TAB:
					compl = qcore.getCompleter().getCompletions(input.getText());
					if (compl.size()==1) {
						input.setText(compl.first());
					} else if (compl.size()>1) {
						input.setText(Completer.greatestCommonPrefix(compl));
						out.println();
						for (String c : compl) {
							out.println(c);
						}
						out.print(prompt);
					}
					break;
				case KeyEvent.VK_ENTER:
					text = tf.getText();
					write(text);
					tf.setText("");
					history.push(text);
					hpointer = history.size();
					break;
				}
			}
        });

		JScrollPane scroll = new JScrollPane(output);
		scroll.setPreferredSize(new Dimension(800, 200));
        this.add(scroll,BorderLayout.CENTER);
        this.add(input,BorderLayout.SOUTH);
	}
	
	public void write(String text) {
		synchronized (qcore) {
			try {
				out.println(text);
				qcore.send(text);
				String rcv = qcore.receiveOrFail();
				out.print(rcv);
			} catch (QuantoCore.ConsoleError e) {
				out.print("ERROR: ".concat(e.getMessage()));
			}
			
			// print the prompt
			out.print(qcore.receive());
		}
	}
	
	public void grabFocus() {
		input.grabFocus();
	}

}