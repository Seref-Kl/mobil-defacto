package com.mobildefacto;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Point;
import java.text.ParseException;
import io.appium.java_client.*;
import com.mobildefacto.helper.StoreHelper;
import com.mobildefacto.model.SelectorInfo;
import com.thoughtworks.gauge.Step;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.touch.offset.ElementOption;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.appium.java_client.touch.WaitOptions.waitOptions;
import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.*;

public class StepImpl extends HookImpl {

    private Logger logger = LoggerFactory.getLogger(getClass());
    public WebDriverWait wait = new WebDriverWait(appiumDriver, 60);

    public StepImpl() {

    }


    public List<MobileElement> findElements(By by) throws Exception {
        List<MobileElement> webElementList = null;
        try {
            webElementList = appiumFluentWait.until(new ExpectedCondition<List<MobileElement>>() {
                @Nullable
                @Override
                public List<MobileElement> apply(@Nullable WebDriver driver) {
                    List<MobileElement> elements = driver.findElements(by);
                    return elements.size() > 0 ? elements : null;
                }
            });
            if (webElementList == null) {
                throw new NullPointerException(String.format("by = %s Web element list not found", by.toString()));
            }
        } catch (Exception e) {
            throw e;
        }
        return webElementList;
    }
    @Step("<key> listedeki son elemente tiklanir")
    public void clickLastElementFromList(String key) {
        try {
            List<MobileElement> list = findElemenstByKey(key);
            list.get(list.size()-1).click();
        }catch (Exception e) {
            throw e;
        }
    }
    public List<MobileElement> findElementsWithoutAssert(By by) {

        List<MobileElement> mobileElements = null;
        try {
            mobileElements = findElements(by);
        } catch (Exception e) {
        }
        return mobileElements;
    }

    public MobileElement findElement(By by) throws Exception {
        MobileElement mobileElement;
        try {
            mobileElement = findElements(by).get(0);
        } catch (Exception e) {
            throw e;
        }
        return mobileElement;
    }

    public boolean doesElementExistByKey(String key) {
        SelectorInfo selectorInfo = selector.getSelectorInfo(key);
        wait.ignoring(NoSuchElementException.class, StaleElementReferenceException.class);
        try {
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(selectorInfo.getBy()));
        } catch (Exception e) {
            Assertions.fail(key + " isimli element bulanamadi");
        }
        return true;
    }

    @Step("<element> elementin value degeri ile <text> icerigi ayni mi")
    public void elementValue(String element,String text) {
        MobileElement eleValue = findElementByKey(element);
        String alinanValueDeger = eleValue.getAttribute("content-desc");
        assertTrue(alinanValueDeger.contains(text));
    }
    public boolean waitPresence(String key) {
        SelectorInfo selectorInfo = selector.getSelectorInfo(key);
        wait.ignoring(NoSuchElementException.class, StaleElementReferenceException.class);
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(selectorInfo.getBy()));
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            Assertions.fail(key + " isimli element yok");
            return false;
        }
        return true;
    }
    @Step("<key> Elementin kaybolması beklenir.")
    public Boolean waitDisappear(String key) {
        SelectorInfo selectorInfo = selector.getSelectorInfo(key);
        try {
            appiumFluentWait = new FluentWait(appiumDriver);
            appiumFluentWait.withTimeout(Duration.ofSeconds(15))
                    .pollingEvery(Duration.ofMillis(150))
                    .ignoring(org.openqa.selenium.NoSuchElementException.class);
            appiumFluentWait.until(ExpectedConditions.invisibilityOfElementLocated(selectorInfo.getBy()));
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            return false;
        }
        return true;
    }
    public void untilElementClickable(MobileElement element) {
        wait.ignoring(NoSuchElementException.class, StaleElementReferenceException.class);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            Assertions.fail(element + " isimli element tıklanabilir değil");

        }
    }

    public MobileElement findElementWithoutAssert(By by) {
        MobileElement mobileElement = null;
        try {
            mobileElement = findElement(by);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mobileElement;
    }

    public WebElement findElementIfExistByKey(String key, int time) { //ecs
        SelectorInfo selectorInfo = selector.getSelectorInfo(key);
        try {
            WebDriverWait wait = new WebDriverWait(appiumDriver, time);
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(selectorInfo.getBy()));
            return element;
        } catch (Exception e) {
            logger.info(key + " aranan element bulunamadı");
            return null;
        }
    }

    public MobileElement findElementWithAssertion(By by) {
        MobileElement mobileElement = null;
        try {
            mobileElement = findElement(by);
        } catch (Exception e) {
            Assertions.fail(mobileElement.getAttribute("value") + " " + "by = %s Element not found ", by.toString());
            e.printStackTrace();
        }
        return mobileElement;
    }

    public MobileElement findElementByKeyWithoutAssert(String key) {
        SelectorInfo selectorInfo = selector.getSelectorInfo(key);
        MobileElement mobileElement = null;
        try {
            mobileElement = selectorInfo.getIndex() > 0 ? findElements(selectorInfo.getBy())
                    .get(selectorInfo.getIndex()) : findElement(selectorInfo.getBy());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mobileElement;
    }

    public MobileElement findElementByKey(String key) {
        SelectorInfo selectorInfo = selector.getSelectorInfo(key);

        MobileElement mobileElement = null;
        try {
            mobileElement = selectorInfo.getIndex() > 0 ? findElements(selectorInfo.getBy())
                    .get(selectorInfo.getIndex()) : findElement(selectorInfo.getBy());
        } catch (Exception e) {
            Assertions.fail("key = %s by = %s Element not found ", key, selectorInfo.getBy().toString());
            e.printStackTrace();
        }
        return mobileElement;
    }


    public List<MobileElement> findElemenstByKey(String key) {
        SelectorInfo selectorInfo = selector.getSelectorInfo(key);
        List<MobileElement> mobileElements = null;
        try {
            mobileElements = findElements(selectorInfo.getBy());
        } catch (Exception e) {
            Assertions.fail("key = %s by = %s Elements not found ", key, selectorInfo.getBy().toString());
            e.printStackTrace();
        }
        return mobileElements;
    }


    @Step({"<key> li elementi bulana kadar swipe et",
            "Find element by <key>  swipe "})
    public void findByKeyWithSwipe(String key) {

        try {
            while (true) {
                TimeUnit.SECONDS.sleep(1);
                if (findElementByKey(key) != null) {
                    swipeDownAccordingToPhoneSize();
                } else {
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step({"<seconds> saniye bekle", "Wait <second> seconds"})
    public void waitBySecond(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void swipeUpAccordingToPhoneSizeECS() {  //ECS
        Dimension size = appiumDriver.manage().window().getSize();
        int startx = size.width / 2;
        int endy = (int) (size.height * 0.20);
        int starty = (int) (size.height * 0.80);

        new TouchAction((AndroidDriver) appiumDriver)
                .press(PointOption.point(startx, starty))
                .waitAction(waitOptions(Duration.ofMillis(1000)))
                .moveTo(PointOption.point(startx, endy))
                .release()
                .perform();
    }

    public void swipeUpAccordingToPhoneSize() {
        if (appiumDriver instanceof AndroidDriver) {
            Dimension d = appiumDriver.manage().window().getSize();
            int height = d.height;
            int width = d.width;
            System.out.println(width + "  " + height);

            int swipeStartWidth = width / 2, swipeEndWidth = width / 2;
            int swipeStartHeight = (height * 20) / 100;
            int swipeEndHeight = (height * 80) / 100;
            //appiumDriver.swipe(swipeStartWidth, swipeStartHeight, swipeEndWidth, swipeEndHeight, 1000);
            new TouchAction((AndroidDriver) appiumDriver)
                    .press(PointOption.point(swipeStartWidth, swipeEndHeight))
                    .waitAction(waitOptions(ofMillis(2000)))
                    .moveTo(PointOption.point(swipeEndWidth, swipeStartHeight))
                    .release()
                    .perform();
        } else {
            Dimension d = appiumDriver.manage().window().getSize();
            int height = d.height;
            int width = d.width;

            int swipeStartWidth = width / 2, swipeEndWidth = width / 2;
            int swipeStartHeight = (height * 35) / 100;
            int swipeEndHeight = (height * 75) / 100;
            //appiumDriver.swipe(swipeStartWidth, swipeStartHeight, swipeEndWidth, swipeEndHeight, 1000);
            new TouchAction(appiumDriver)
                    .press(PointOption.point(swipeStartWidth, swipeEndHeight))
                    .waitAction(waitOptions(ofMillis(2000)))
                    .moveTo(PointOption.point(swipeEndWidth, swipeStartHeight))
                    .release()
                    .perform();
        }
    }

    public void swipeDownAccordingToPhoneSize() {
        if (appiumDriver instanceof AndroidDriver) {
            Dimension d = appiumDriver.manage().window().getSize();
            int height = d.height;
            int width = d.width;

            int swipeStartWidth = width / 2, swipeEndWidth = width / 2;
            int swipeStartHeight = (height * 90) / 100;
            int swipeEndHeight = (height * 50) / 100;
            //appiumDriver.swipe(swipeStartWidth, swipeStartHeight, swipeEndWidth, swipeEndHeight, 1000);
            new TouchAction(appiumDriver)
                    .press(PointOption.point(swipeStartWidth, swipeStartHeight))
                    .waitAction(waitOptions(ofMillis(1000)))
                    .moveTo(PointOption.point(swipeEndWidth, swipeEndHeight))
                    .release()
                    .perform();
        } else {
            Dimension d = appiumDriver.manage().window().getSize();
            int height = d.height;
            int width = d.width;

            int swipeStartWidth = width / 2, swipeEndWidth = width / 2;
            int swipeStartHeight = (height * 90) / 100;
            int swipeEndHeight = (height * 40) / 100;
            // appiumDriver.swipe(swipeStartWidth, swipeStartHeight, swipeEndWidth, swipeEndHeight, 1000);
            new TouchAction(appiumDriver)
                    .press(PointOption.point(swipeStartWidth, swipeStartHeight))
                    .waitAction(waitOptions(ofMillis(1000)))
                    .moveTo(PointOption.point(swipeEndWidth, swipeEndHeight))
                    .release()
                    .perform();
        }
    }

    public void swipeDownInMiddle() {
        if (appiumDriver instanceof AndroidDriver) {
            Dimension d = appiumDriver.manage().window().getSize();
            int height = d.height;
            int width = d.width;

            int swipeStartWidth = width / 2, swipeEndWidth = width / 2;
            int swipeStartHeight = (height * 50) / 100;
            int swipeEndHeight = (height * 20) / 100;
            //appiumDriver.swipe(swipeStartWidth, swipeStartHeight, swipeEndWidth, swipeEndHeight, 1000);
            new TouchAction(appiumDriver)
                    .press(PointOption.point(swipeStartWidth, swipeStartHeight))
                    .waitAction(waitOptions(ofMillis(1000)))
                    .moveTo(PointOption.point(swipeEndWidth, swipeEndHeight))
                    .release()
                    .perform();
        } else {
            Dimension d = appiumDriver.manage().window().getSize();
            int height = d.height;
            int width = d.width;

            int swipeStartWidth = width / 2, swipeEndWidth = width / 2;
            int swipeStartHeight = (height * 90) / 100;
            int swipeEndHeight = (height * 40) / 100;
            // appiumDriver.swipe(swipeStartWidth, swipeStartHeight, swipeEndWidth, swipeEndHeight, 1000);
            new TouchAction(appiumDriver)
                    .press(PointOption.point(swipeStartWidth, swipeStartHeight))
                    .waitAction(waitOptions(ofMillis(1000)))
                    .moveTo(PointOption.point(swipeEndWidth, swipeEndHeight))
                    .release()
                    .perform();
        }
    }
    public void swipe(int startX, int startY, int endX, int endY, int duration) {
        new TouchAction(appiumDriver)
                .press(PointOption.point(startX, startY))
                .waitAction(waitOptions(ofMillis(duration)))
                .moveTo(PointOption.point(endX, endY))
                .release()
                .perform();
    }

    @Step({"<key> li elementi bul ve temizle", "Find element by <key> and clear"})
//ecs
    public void clearKey(String key) {
        MobileElement webElement = findElementByKey(key);
        logger.info("Element bulundu: " + webElement.toString());
// Elemente dokun
        new TouchAction(appiumDriver).tap(ElementOption.element(webElement)).perform();
        logger.info("Elemente tıklandı: " + webElement.toString());
// Elementin içeriğini temizle
        webElement.clear();
        logger.info("Elementin içeriği temizlendi.");
        waitBySecond(3);
//Elementin içeriğini temizle
        webElement.clear();
        logger.info("Elementin içeriği 2. kez tamamen temizlendi.");
    }

    @Step({"Klavyeyi kapat", "Hide keyboard"})
    public void hideKeyboard() {
        try {
            if (localAndroid == false) { // iOS için
                List<MobileElement> doneButtons = appiumDriver.findElements(By.xpath("//XCUIElementTypeButton[@name='Done']"));
                if (!doneButtons.isEmpty()) {
                    doneButtons.get(0).click();
                } else {
                    List<MobileElement> toolbarDoneButtons = appiumDriver.findElements(By.xpath("//XCUIElementTypeButton[@name='Toolbar Done Button']"));
                    if (!toolbarDoneButtons.isEmpty()) {
                        toolbarDoneButtons.get(0).click();
                    } else {
                        logger.error("iOS'ta klavye kapatma butonu bulunamadı.");
                    }
                }
            } else {
                appiumDriver.hideKeyboard();
            }

        } catch (Exception ex) {
            logger.error("Klavye kapatılamadı. Hata: " + ex.getMessage());
        }
    }



    @Step({"<key> li elementi bul ve değerini <saveKey> saklanan değer ile karşılaştır", "Find element by <key> and compare saved key <saveKey>"})
    public void equalsSaveTextByKey(String key, String saveKey) {
        assertEquals(StoreHelper.INSTANCE.getValue(saveKey), findElementByKey(key).getText());
        logger.info("Element by " + key + " is equal to " + StoreHelper.INSTANCE.getValue(saveKey));
        logger.info("Element by " + key + " contains: " + findElementByKey(key).getText() + " and Equals to " + saveKey + ": " + StoreHelper.INSTANCE.getValue(saveKey));
    }

    @Step({"<key> li elementi bul ve değerini <saveKey> boşlukları silip saklanan değer ile karşılaştır"})
    public void equalsIgnoreSpaceSaveTextByKey(String key, String saveKey) {
        assertEquals(StoreHelper.INSTANCE.getValue(saveKey).replaceAll("\\s+", ""), findElementByKey(key).getText());
        logger.info("Element by " + key + " is equal to " + StoreHelper.INSTANCE.getValue(saveKey));
        logger.info("Element by " + key + " contains: " + findElementByKey(key).getText() + " and Equals to " + saveKey + ": " + StoreHelper.INSTANCE.getValue(saveKey));
    }


    @Step({"<text> li text bulana kadar swipe et",
            "Find text by <text>  swipe "})
    public void findByTextWithSwipe(String text) {
        boolean isElementFound = false;

        while (!isElementFound) {
            try {
                MobileElement element = findElementWithAssertion(By.xpath("(.//*[contains(@text,'" + text + "')])[1]"));
                if (appiumDriver instanceof AndroidDriver){
                    logger.info("Android için swipe");
                    if (element.isDisplayed()) {
                        logger.info(element + " görüldü!!");
                        isElementFound = true;
                    } else {
                        swipeDownAccordingToPhoneSize();
                        logger.info("swipe yapıldı!!");
                    }
                }
                else {
                    logger.info("IOS için swipe");
                    if (element.isDisplayed()) {
                        logger.info(element + " görüldü!!");
                        isElementFound = true;
                    } else {
                        scrollDownToIOS();
                        logger.info("swipe yapıldı!!");
                    }
                }

            } catch (Exception e) {
                if (appiumDriver instanceof AndroidDriver)
                    swipeDownAccordingToPhoneSize();
                else
                    scrollDownToIOS();
            }
        }
    }

    @Step({"<key> li elementi bul ve değerini <saveKey> olarak sakla",
            "Find element by <key> and save text <saveKey>"})
    public void saveTextByKey(String key, String saveKey) {
        StoreHelper.INSTANCE.saveValue(saveKey, findElementByKey(key).getText());
        logger.info(saveKey + " olarak " + StoreHelper.INSTANCE.getValue(saveKey) + " değeri başarıyla saklandı.");

    }

    @Step({"Elementine tıkla <key>", "Click element by <key>"})
    public void clickByKey(String key) {
        doesElementExistByKey(key);
        try {
            findElementByKey(key).click();
            logger.info(key + "elemente tıkladı");
        }catch(Exception e){
            Assertions.fail(key + "elementine tiklamadi",e.getMessage());
        }

    }
    @Step({"<key> Elementi varsa tıkla"})
    public void displayElementClick(String key) {
        MobileElement element;
        element = findElementByKeyWithoutAssert(key);
        if(element != null ){
            long startTime = System.currentTimeMillis();
            element.click();
            long endTime = System.currentTimeMillis();
            long clickTime = endTime - startTime;
            long clickTimeSeconds = clickTime / 1000;
            logger.info("tiklamanin"+" "+ clickTimeSeconds + ". saniyesinde "+ key +"elementine tıklandı");
        }else{
            logger.error(key + " " + "elementi bulunamadi");
        }
    }

    public boolean checkDisplayElement(String key) {
        MobileElement element;
        element = findElementByKeyWithoutAssert(key);
        if(element != null ){
            logger.info(key +"elementine bulundu");
            return true;
        }else{
            logger.error(key + " " + "elementi bulunamadi");
            return false;
        }
    }

    @Step({"<key> li elementi bul ve <text> değerini yaz",
            "Find element by <key> and set value or send keys <text>"})
    public void setValueOrSendKeys(String key, String text) {
        MobileElement webElement = findElementByKey(key);

        try {
            new TouchAction(appiumDriver).tap(ElementOption.element(webElement)).perform();
            appiumDriver.getKeyboard().sendKeys(text);
            logger.info("sendKeysByKeyAction ile yazıldı: " + text);
        } catch (Exception e1) {
            logger.error("sendKeysByKeyAction başarısız oldu. Hata: " + e1.getMessage());

            try {
                webElement.click();
                webElement.setValue(text);
                logger.info("setValue ile yazıldı: " + text);
            } catch (Exception e2) {
                logger.error("setValue ile de başarısız oldu. Hata: " + e2.getMessage());
                logger.error("Her iki yöntemle de yazma işlemi başarısız oldu.");
            }
        }
    }
    @Step({"<key> li elementi bul ve <key> alanina rastgele deger olustur"})
    public void setValueRandomAccordingly(String key, String whichField) {
        MobileElement webElement = findElementByKey(key);
        String eklenecekValue = null;
        try {
            if(appiumDriver instanceof AndroidDriver) {
                new TouchAction(appiumDriver).tap(ElementOption.element(webElement)).perform();
            }else{
                webElement.click();
            }
            webElement.clear();
            if(whichField.equals("mail")) {
                eklenecekValue = "testmail" + generateRandomInt(1,1000) + "@hotmail.com";
                for (int i = 0; i < eklenecekValue.length() ; i++) {
                    char letter = eklenecekValue.charAt(i);
                    logger.info("<<<<<<<<<<<"+letter+">>>>>>>>>>>>>>>>>>");
                    appiumDriver.getKeyboard().sendKeys(Character.toString(letter));
                }
                logger.info("mail alanı eklendi." );
            }else if(whichField.equals("isyeriadi")) {
                eklenecekValue = "halkbank" + generateRandomInt(1,1000);
                appiumDriver.getKeyboard().sendKeys(eklenecekValue);

            }else if(whichField.equals("kurus")){
                eklenecekValue = String.valueOf(generateRandomInt(1,99));
                appiumDriver.getKeyboard().sendKeys(eklenecekValue);
            }
            else { // başka field eklenmek isteniyorsa son iften else if açıp equal ile kontrol ediniz.
                appiumDriver.getKeyboard().sendKeys("test");
            }
        } catch (Exception e1) {
            logger.error("sendKeysByKeyAction başarısız oldu. Hata: " + e1.getMessage());
            try {
                webElement.click();
                webElement.setValue("test");
            } catch (Exception e2) {
                logger.error("setValue ile de başarısız oldu. Hata: " + e2.getMessage());
                logger.error("Her iki yöntemle de yazma işlemi başarısız oldu.");
            }
        }
    }

    @Step({"<key> li elementi bul ve listeden rastgele sec"})
    public void findElementsAndClickRandomly(String key) {

        List<MobileElement> list = findElemenstByKey(key);
        int min = 1;
        int max = list.size()-1;
        int randomNumber = generateRandomInt(min,max); // min ve max dahil !
        try {
            logger.info("List Size : " + String.valueOf(max));
            logger.info("Random Number From List : " + String.valueOf(randomNumber));
            Thread.sleep(2000);
            list.get(randomNumber).click();
        } catch (Exception e1) {
            logger.error("sendKeysByKeyAction başarısız oldu. Hata: " + e1.getMessage());
        }
    }
    public int generateRandomInt(int min,int max) {
        Random rastgele = new Random();
        int randomNum = rastgele.nextInt(max - min + 1) + min;
        return randomNum;
    }

    @Step({"<key> elementinin görünürlüğü kontrol edilir"})
    public void existElement(String key) {
        assertTrue(findElementByKey(key).isDisplayed(), "Element sayfada bulunamadı!");
        logger.info( key + " elementi sayfada göründü" );

    }

    @Step("<key> li elementi rasgele sec")
    public void chooseRandomProduct(String key) {

        List<MobileElement> productList = new ArrayList<>();
        List<MobileElement> elements = findElemenstByKey(key);
        int elementsSize = elements.size();
        int height = appiumDriver.manage().window().getSize().height;
        for (int i = 0; i < elementsSize; i++) {
            MobileElement element = elements.get(i);
            int y = element.getCenter().getY();
            if (y > 0 && y < (height - 100)) {
                productList.add(element);
            }
        }
        Random random = new Random();
        int randomNumber = random.nextInt(productList.size());
        productList.get(randomNumber).click();
    }

    @Step({"<t> textini <k> elemente yaz",
            "Find element by <key> and send keys <text>"})
    public void sendKeysByKeyNotClear(String t, String k) {
        waitPresence(k);
        doesElementExistByKey(k);
        findElementByKey(k).clear();
        findElementByKey(k).sendKeys(t);
    }

    @Step({"<text> değerini sayfa üzerinde olup olmadığını kontrol et"})
    public void getPageSourceFindWord(String text) {
        assertTrue(appiumDriver.getPageSource().contains(text), text + " sayfa üzerinde bulunamadı."
        );
        logger.info(text + " sayfa üzerinde bulundu");
    }
    @Step({"<text> değerinin sayfa üzerinde olmadigini kontrol et"})
    public void validateWithAssertFalse(String text) {
        assertFalse(appiumDriver.getPageSource().contains(text),"Beklenmedik bir text ile karşılaşıldı : \"" +text+"\" sayfa üzerinde bulundu !"
        );
        logger.info("\"" +text+"\" yazısı sayfada görüntülenmedi. (Expected)");
    }
    @Step("<key> elementinin <text> textini içerdiği kontrol edilir")
    public void checkTextByKey(String key, String text) {
        try {
            Thread.sleep(2000);
            assertTrue(findElementByKey(key).getText().contains(text), "Element beklenen değeri taşımıyor !");
            logger.info(key + " elementi kontrol edildi ve " + text + " metni içerdiği doğrulandı.");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step("Uygulamayı yeniden başlat")
    public void restartApp() {
        try {
            // Uygulamayı kapat
            appiumDriver.closeApp();
            logger.info("Uygulama başarıyla kapatıldı.");
            Thread.sleep(3000);
            appiumDriver.launchApp();
            logger.info("Uygulama başarıyla yeniden başlatıldı.");

        } catch (Exception e) {
            logger.error("Uygulamayı yeniden başlatma sırasında bir hata oluştu: " + e.getMessage());
        }
    }
    @Step("Uygulamayı yenile")
    public void refreshApp() {
        try {
            // Uygulamayı belirli bir süre için arka plana gönder
            appiumDriver.runAppInBackground(Duration.ofSeconds(50));
            logger.info("Uygulama arka planda çalıştırıldı ve yenilendi.");

        } catch (Exception e) {
            logger.error("Uygulamanın yenilenmesi sırasında bir hata oluştu: " + e.getMessage());
        }
    }
    @Step({"Enter'a tıkla", "Click enter"})
    public static void pressEnterKey() {
        if (appiumDriver instanceof AndroidDriver) {
            pressEnterAndroid((AndroidDriver) appiumDriver);
        } else if (appiumDriver instanceof IOSDriver) {
            pressEnterIOS((IOSDriver) appiumDriver);
        } else {
            System.out.println("Unsupported platform");
        }

    }
    @Step("<key> elementinin icerigini sil ve <text> yazisini gonder")
    public void clearAndSendKeys(String key, String text) {
        SelectorInfo ele = selector.getSelectorInfo(key);

        MobileElement element = findElementWithAssertion(ele.getBy());
        if(element != null ){
            element.click();
            element.clear();
            element.sendKeys(text);
            logger.info(key + " elementinin içeriği silindi ve " + text + " metni gönderildi");
            logger.info( element + "element yazıldı");
        }else {
            logger.error("element yok");
        }

    }



    private static void pressEnterAndroid(AndroidDriver androidDriver) {
        androidDriver.pressKey(new KeyEvent(AndroidKey.ENTER));
    }

    private static void pressEnterIOS(IOSDriver iosDriver) {
        iosDriver.getKeyboard().sendKeys(Keys.RETURN);
    }


    @Step("<key> anahtarlı elementin görünürlüğünü kontrol et")
    public boolean checkElementVisibilityByKey(String key) {
        try {
            MobileElement element = findElementByKey(key);
            boolean isDisplayed = element.isDisplayed();
            if (isDisplayed) {
                logger.info(key + " anahtarlı element görünür.");
            } else {
                logger.error(key + " anahtarlı element görünür değil.");
            }
            return isDisplayed;
        } catch (Exception e) {
            logger.error(key + " anahtarlı element kontrolü sırasında hata oluştu.", e);
            return false;
        }
    }

    @Step("<key> elementinin <content-desc> content-desc içerdiği kontrol edilir")
    public void checkContentDescBys(String key, String text) {
        try {
            Thread.sleep(3000);
            MobileElement element = findElementByKey(key);
            String deger = element.getAttribute("content-desc");
            assertTrue(deger.contains(text), "Element beklenen değeri taşımıyor !");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step("<key> anahtarlı elementte <expectedText> metnini doğrula")    //eyupcansonmez
    public void assertTextInElement(String key, String expectedText) {
        MobileElement element = findElementByKey(key);
        String actualText = element.getText();
        logger.error(expectedText, actualText, "Elementin metni beklenen ile eşleşmiyor.");
    }

    @Step("<saveKey> kayıtlı değer ile <key> elementinin değerini karşılaştır")
    public void compareSavedValueWithElementText(String saveKey, String key) {
        String savedValue = StoreHelper.INSTANCE.getValue(saveKey); // saklanan değeri al
        logger.info(savedValue);
        String elementText = findElementByKey(key).getText(); // yeni elementin metnini al
        logger.info(elementText);

        assertTrue(savedValue.equals(elementText), "Kaydedilen değer (" + savedValue + ") ile elementin değeri (" + elementText + ") uyuşmuyor!");

        if (savedValue.equals(elementText)) {
            logger.info("Kaydedilen değer ile elementin değeri uyuşuyor: " + savedValue);
        } else {
            logger.error("Kaydedilen değer" + savedValue + "ile elementin değeri" + elementText + " uyuşmuyor!");
        }
    }


    public void swipeUpSpeedAccordingToPhoneSize() {
        if (appiumDriver instanceof AndroidDriver) {
            Dimension d = appiumDriver.manage().window().getSize();
            int height = d.height;
            int width = d.width;
            System.out.println(width + "  " + height);

            int swipeStartWidth = width / 2, swipeEndWidth = width / 2;
            int swipeStartHeight = (height * 5) / 100;
            int swipeEndHeight = (height * 80) / 100;
            //appiumDriver.swipe(swipeStartWidth, swipeStartHeight, swipeEndWidth, swipeEndHeight, 1000);
            new TouchAction((AndroidDriver) appiumDriver)
                    .press(PointOption.point(swipeStartWidth, swipeEndHeight))
                    .waitAction(waitOptions(ofMillis(2000)))
                    .moveTo(PointOption.point(swipeEndWidth, swipeStartHeight))
                    .release()
                    .perform();
        } else {
            Dimension d = appiumDriver.manage().window().getSize();
            int height = d.height;
            int width = d.width;

            int swipeStartWidth = width / 2, swipeEndWidth = width / 2;
            int swipeStartHeight = (height * 5) / 100;
            int swipeEndHeight = (height * 75) / 100;
            //appiumDriver.swipe(swipeStartWidth, swipeStartHeight, swipeEndWidth, swipeEndHeight, 1000);
            new TouchAction(appiumDriver)
                    .press(PointOption.point(swipeStartWidth, swipeEndHeight))
                    .waitAction(waitOptions(ofMillis(2000)))
                    .moveTo(PointOption.point(swipeEndWidth, swipeStartHeight))
                    .release()
                    .perform();
        }
    }
    @Step("<key> element bulunasiya kadar asagi kaydır")
    public void swipeDownFindElement(String key){
        MobileElement element;
        for(int i=0;i<20;i++){
            element = findElementByKeyWithoutAssert(key);
            if (appiumDriver instanceof IOSDriver){
                if (element == null){
                    scrollDownToIOS();
                    logger.info("asagi swipe yapıldı");
                }else
                    break;

            }
            else {
                if (element == null){
                    scrollDownAndroid();
                    logger.info("asagi swipe yapıldı");
                }else
                    break;

            }

        }
    }
    @Step("<kez> kez asagi kaydır")
    public void swipeDown(int kez){
        for (int i = 0; i<kez; i++){
            if (appiumDriver instanceof IOSDriver){
                scrollDownToIOS();
                logger.info("asagi swipe yapıldı");
            }
            else {
                scrollDownAndroid();
                logger.info("asagi swipe yapıldı");
            }

        }
    }
    @Step("<kez> kez yukari kaydır")
    public void swipeUp(int kez){
        for (int i = 0; i < kez; i++){
            if (appiumDriver instanceof IOSDriver){
                scrollUpToIOS();
                logger.info("yukari swipe yapıldı");

            }else {
                scrollUpAndroid();
                logger.info("yukari swipe yapıldı");

            }

        }

    }
    public void scrollUpToIOS() {
        JavascriptExecutor js = appiumDriver;
        HashMap<String, String> scrollObject = new HashMap<>();
        scrollObject.put("direction", "up");
        js.executeScript("mobile: scroll", scrollObject);
    }

    public void scrollDownToIOS() {
        JavascriptExecutor js = appiumDriver;
        HashMap<String, String> scrollObject = new HashMap<>();
        scrollObject.put("direction", "down");
        js.executeScript("mobile: scroll", scrollObject);
    }

    public void scrollUpAndroid(){
        Dimension d = appiumDriver.manage().window().getSize();
        int height = d.height;
        int width = d.width;
        System.out.println(width + "  " + height);

        int swipeStartWidth = width / 2, swipeEndWidth = width / 2;
        int swipeStartHeight = (height * 20) / 100;
        int swipeEndHeight = (height * 80) / 100;

        new TouchAction(appiumDriver)
                .press(PointOption.point(swipeEndWidth, swipeStartHeight))
                .waitAction(WaitOptions.waitOptions(ofMillis(2000)))
                .moveTo(PointOption.point(swipeStartWidth, swipeEndHeight))
                .release()
                .perform();
    }
    public void scrollDownAndroid(){
        Dimension d = appiumDriver.manage().window().getSize();
        int height = d.height;
        int width = d.width;
        System.out.println(width + "  " + height);

        int swipeStartWidth = width / 2, swipeEndWidth = width / 2;
        int swipeStartHeight = (height * 20) / 100;
        int swipeEndHeight = (height * 80) / 100;

        new TouchAction(appiumDriver)
                .press(PointOption.point(swipeStartWidth, swipeEndHeight))
                .waitAction(WaitOptions.waitOptions(ofMillis(2000)))
                .moveTo(PointOption.point(swipeEndWidth, swipeStartHeight))
                .release()
                .perform();
    }

    @Step("Uygulamayı kapat")
    public void kptUyg() {
        appiumDriver.closeApp();
        logger.info("Uygulama başarıyla kapatıldı.");

    }
    @Step("<key> elementinin <text> içerdiği kontrol edilir")
    public void checkContentDescByKey(String key, String text) {
        try {
            Thread.sleep(3000);
            MobileElement element = findElementByKey(key);
            String deger = element.getAttribute("content-desc");
            logger.info(deger);
            assertTrue(deger.contains(text), "Element beklenen değeri taşımıyor !");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Step("Saga kaydır")
    public void rightSwipe(){
        Dimension size = appiumDriver.manage().window().getSize();
        int startx = (int) (size.width * 0.80);
        logger. info("startx: "+ startx);
        int endx = (int) (size.width * 0.20);
        logger. info("endx: "+ endx);
        int starty = (int) (size.height * 0.80);
        logger. info("starty: "+ starty);
        new TouchAction(appiumDriver)
                .press(PointOption.point(startx, starty))
                .waitAction(WaitOptions.waitOptions(ofMillis(2000)))
                .moveTo(PointOption.point(endx, starty))
                .release()
                .perform();

    }
    @Step("<key> elementine farklı tıkla")
    public void clickDene(String key){
        MobileElement element = findElementByKey(key);
        Point point = element.getLocation();
        logger.info("point: " + point);
        int x = point.x + element.getSize().getWidth() - 50;
        int y = point.y + element.getSize().getHeight() - 50;
        logger.info("x: " + x + "y: " + y);
        new TouchAction(appiumDriver).tap(PointOption.point(x, y)).perform();
    }

}