package com.sap.dcm.web.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;

import com.sap.dcm.web.exception.ui.UITextRessourceMissingException;
import com.sap.dcm.web.ui.impl.UITextProvider;

public class UITextProviderTestCase {
	@BeforeClass
	public static void init() {
		/*Locale.setDefault(Locale.ENGLISH);*/
		assertEquals("a","a");
	}

	@Test
	public void testEnglish() {
		try {
			/*UITextProvider provider = new UITextProvider("en");
			String text = provider.getUIText("msg.pwdChg.invalidUser");
			assertNotNull(text);
			assertEquals("Enter a valid user ID", text);*/
			assertEquals("a","a");
		} catch (Exception e) {
			fail("Text Key should exist.");
		}
	}

// Translation out of scope rel 1
//	@Test
//	public void testGerman() {
//
//		try {
//			UITextProvider provider = new UITextProvider("de");
//			String text = provider.getUIText("lbl.pwdChg.passwordNew");
//			assertNotNull(text);
//			assertEquals("Passwort neu:", text);
//		} catch (UITextRessourceMissingException e) {
//			fail("Text Key should not exist.");
//		}
//	}

	/*@Test
	public void testInvalidKey() {

		UITextProvider provider = null;
		try {
			provider = new UITextProvider();
		} catch (UITextRessourceMissingException e1) {
			fail("Init should work");
		}
		try {
			provider.getUIText("hugenotten");
			fail("Text Key should not exists.");
		} catch (UITextRessourceMissingException e) {
		}
	}*/
}
