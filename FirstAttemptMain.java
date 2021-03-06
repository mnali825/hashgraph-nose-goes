
/*
 * This file is public domain.
 *
 * SWIRLDS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF 
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SWIRLDS SHALL NOT BE LIABLE FOR 
 * ANY DAMAGES SUFFERED AS A RESULT OF USING, MODIFYING OR 
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

import java.nio.charset.StandardCharsets;

import com.swirlds.platform.Browser;
import com.swirlds.platform.Console;
import com.swirlds.platform.Platform;
import com.swirlds.platform.SwirldMain;
import com.swirlds.platform.SwirldState;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * This HelloSwirld creates a single transaction, consisting of the string "Hello Swirld", and then goes
 * into a busy loop (checking once a second) to see when the state gets the transaction. When it does, it
 * prints it, too.
 */
public class FirstAttemptMain implements SwirldMain {
	/** the platform running this app */
	public Platform platform;
	/** ID number for this member */
	public int selfId;
	/** a console window for text output */
	public Console console;
	/** sleep this many milliseconds after each sync */
	public final int sleepPeriod = 100;

	/**
	 * This is just for debugging: it allows the app to run in Eclipse. If the config.txt exists and lists a
	 * particular SwirldMain class as the one to run, then it can run in Eclipse (with the green triangle
	 * icon).
	 * 
	 * @param args
	 *            these are not used
	 */
	public static void main(String[] args) {
		Browser.main(null);
	}

	// ///////////////////////////////////////////////////////////////////

	@Override
	public void preEvent() {
	}
	
	JFrame frame;
	JLabel label2;
	
	@Override
	public void init(Platform platform, int id) {
		this.platform = platform;
		this.selfId = id;
//		this.console = platform.createConsole(true); // create the window, make it visible
		frame=new JFrame("Nose Goes");
        
		// creates instance of JButton
        JButton btn = new JButton("Nose Goes");
        btn.setBounds(20, 200, 260, 50);
        
        JLabel label = new JLabel("Consensus Order:", SwingConstants.CENTER);
        label.setBounds(10,250,280,100);
        
        label2 = new JLabel();
        label2.setBounds(10,280,280,100);
        
        // setting close operation
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // adds button in JFrame
        frame.add(btn);
        frame.add(label);
        frame.add(label2);
        // sets 500 width and 600 height
        frame.setSize(300, 500);
        // uses no layout managers
        frame.setLayout(null);
        // makes the frame visible
        frame.setVisible(true);
        
        String myName = platform.getState().getAddressBookCopy()
				.getAddress(selfId).getSelfName();
        
		// create a transaction. For this example app,
		// we will define each transactions to simply
		// be a string in UTF-8 encoding.
		byte[] transaction = myName.getBytes(StandardCharsets.UTF_8);
        
        btn.addActionListener(new ActionListener() {
        		@Override
        		public void actionPerformed(ActionEvent arg0) {
        			// Send the transaction to the Platform, which will then
        			// forward it to the State object.
        			// The Platform will also send the transaction to
        			// all the other members of the community during syncs with them.
        			// The community as a whole will decide the order of the transactions
        			platform.createTransaction(transaction, null);
        		}
        });
        
		platform.setAbout("Hello Swirld v. 1.0\n"); // set the browser's "about" box
		platform.setSleepAfterSync(sleepPeriod);
	}

	@Override
	public void run() {
		
		// console.out.println("Hello Swirld from " + myName);

		String lastReceived = "";

		while (true) {
			FirstAttemptState state = (FirstAttemptState) platform
					.getState();
			String received = state.getReceived();

			if (!lastReceived.equals(received)) {
				lastReceived = received;
//				console.out.println("Received: " + received); // print all received transactions
				label2.setText(received);
			}
			try {
				Thread.sleep(sleepPeriod);
			} catch (Exception e) {
			}
		}
	}

	@Override
	public SwirldState newState() {
		return new FirstAttemptState();
	}
}