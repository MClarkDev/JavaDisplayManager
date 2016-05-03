package org.jdm.display.panel;

import java.awt.Graphics;

import javax.swing.JEditorPane;

import org.jdm.core.Panel;

public class WebPanel extends Panel {

	private String url;

	private JEditorPane editorPane;

	public WebPanel() {
		editorPane = new JEditorPane();
		editorPane.setContentType("text/html");
		// editorPane.setText( "<html><body>hi</body></html>" );
		add(editorPane);
	}

	@Override
	protected void onDraw(Graphics g) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onUpdate(String string) {
		this.url = string;

		try {
			editorPane.setPage(url);

			editorPane.setSize(getWidth(), getHeight());
			editorPane.setLocation(0, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
