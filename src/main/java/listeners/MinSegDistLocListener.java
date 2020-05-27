package listeners;

import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import pluginTools.InteractiveEmbryo;

public class MinSegDistLocListener implements TextListener {

	final InteractiveEmbryo parent;

	boolean pressed;

	public MinSegDistLocListener(final InteractiveEmbryo parent, final boolean pressed) {

		this.parent = parent;
		this.pressed = pressed;

	}


	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent) e.getSource();
		String s = tc.getText();
		if (s.length() > 0) {
			parent.minSegDist = Integer.parseInt(s);

			parent.minSegDistText.setText(parent.minSegDiststring + " = " + parent.minSegDist);
			parent.maxSegDist = Math.max(parent.minSegDist, parent.maxSegDist);
			parent.minSegDistslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.minSegDist,
					0, parent.maxSegDist, parent.scrollbarSize));
			parent.minSegDistslider.repaint();
			parent.minSegDistslider.validate();

		}
		tc.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {

			}

			@Override
			public void keyReleased(KeyEvent arg0) {

				if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {

					pressed = false;

				}

			}

			@Override
			public void keyPressed(KeyEvent arg0) {

				if (arg0.getKeyChar() == KeyEvent.VK_ENTER && !pressed) {
					pressed = true;

					parent.StartDisplayer();

				}

			}
		});

	}

}


