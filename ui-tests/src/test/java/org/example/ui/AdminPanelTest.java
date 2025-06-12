package org.example.ui;

import org.example.ui.pages.AdminPanelPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AdminPanelTest extends BaseTest {
    private static AdminPanelPage adminPage;

    @BeforeAll
    public static void setup() {
        BaseTest.setup();
        adminPage = new AdminPanelPage(driver);
    }

    @Test
    public void getTitle(){
        Assertions.assertEquals("Highlights", adminPage.getTitle());
    }
}