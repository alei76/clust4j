package com.clust4j.log;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.clust4j.log.Log.LogWrapper;
import com.clust4j.log.LogTimeFormatter.TimeSlots;

public class LogTest {
	final static String file = new String("tmpbmrtfile.csv");
	final static Path path = FileSystems.getDefault().getPath(file);

	@Test
	public void test() {
		Log.info("Test1");
		assertTrue(true);
	}
	
	static void touchFile() throws FileNotFoundException, IOException {
		new FileOutputStream(new File(file)).close();
	}
	
	@Test
	public void testLogWrapper() throws IOException {
		LogWrapper l = null;
		
		try {
			touchFile();
			PrintStream p = new PrintStream(new File(file));
			l = new LogWrapper(p);
			
			l.printf("asdf", "asdf");
			l.printf(Locale.ENGLISH, "asdf", "asdf");
			l.println("ln");
			l.printlnParent("prnt");
			
		} finally {
			try {
				Files.delete(path);
			} catch(Exception e){}
			
			try{
				l.close();
			} catch(Exception e){}
		}
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testFormatterIAE() {
		LogTimeFormatter.subtractAmt(System.currentTimeMillis(), TimeUnit.MICROSECONDS, TimeUnit.MICROSECONDS);
	}
	
	@Test
	public void testTimeSlots() {
		TimeSlots t = new LogTimeFormatter.TimeSlots(1, 1, 1, 1, 1);
		String millis = LogTimeFormatter.millis(t, true);
		System.out.println("Truncated:\t" + millis);
		millis = LogTimeFormatter.millis(t, false);
		System.out.println("Un-truncated:\t" + millis);
		
		// Where hr == 0 && min != 0
		t = new LogTimeFormatter.TimeSlots(0, 1, 1, 1, 1);
		millis = LogTimeFormatter.millis(t, true);
		System.out.println("Truncated:\t" + millis);
		millis = LogTimeFormatter.millis(t, false);
		System.out.println("Un-truncated:\t" + millis);
		
		// Where hr == 0 && min == 0
		t = new LogTimeFormatter.TimeSlots(0, 0, 1, 1, 1);
		millis = LogTimeFormatter.millis(t, true);
		System.out.println("Truncated:\t" + millis);
		millis = LogTimeFormatter.millis(t, false);
		System.out.println("Un-truncated:\t" + millis);
	}
	
	@Test
	public void testLogTimer() throws InterruptedException {
		final LogTimer timer = new LogTimer();
		assertTrue(timer.time() >= 0);
		assertTrue(timer.nanos() >= 0);
		
		long start = System.currentTimeMillis();
		Thread.sleep(100);
		long end = System.currentTimeMillis();
		System.out.println(timer.formatTime(start, end));
		System.out.println("Start short string: " + timer.startAsShortString());
		System.out.println("Now as short string: " + timer.nowAsShortString());
		System.out.println("Now as long string: " + timer.nowAsString());
	}
}
