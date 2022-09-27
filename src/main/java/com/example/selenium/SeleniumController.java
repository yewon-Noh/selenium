package com.example.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class SeleniumController {

//    private String URL = "https://cse.inhatc.ac.kr/cse/2206/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGY3NlJTJGMTA3JTJGYXJ0Y2xMaXN0LmRvJTNG";
    private String URL = "https://www.inhatc.ac.kr/kr/461/subview.do";
    private String Login_URL = "https://portal.inhatc.ac.kr/user/login.face";

    @Value("${inhatc.user_id}")
    private String user_id;

    @Value("${inhatc.user_pwd}")
    private String user_pwd;

    @GetMapping("")
    public String selenium() {
        String result = "";

        System.out.println("####START####");

        Path path = Paths.get("D:\\chromedriver_win32\\chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", path.toString());

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-popup-blocking");   // 팝업 안띄움
        options.addArguments("headless");   // 브라우저 안띄움
        options.addArguments("--disable-gpu");  // gpu 비활성화
        options.addArguments("--blink-settings=imagesEnabled=false");   // 이미지 다운 안받음

        WebDriver driver = new ChromeDriver(options);

        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));    // 드라이버가 실행된 후 10초 기다림

        driver.get(URL);

//        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); // 페이지 전체가 로딩될때까지 기다림

        webDriverWait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("div._fnctWrap > form:nth-child(2) > div > table > tbody > tr"))
                //cssSelector로 선택한 부분이 로딩될때까지 기다림
        );

        List<WebElement> contents = driver.findElements(By.cssSelector("div._fnctWrap > form:nth-child(2) > div > table > tbody > tr"));
        result += "조회된 공지 수 : " + contents.size() + "<br />";
        if(contents.size() > 0) {
            for (WebElement content : contents) {
                String title = content.findElement(By.cssSelector("td.td-subject > a > strong")).getText();
                String date = content.findElement(By.cssSelector("td.td-date")).getText();
                result += "공지 title / 작성일 : " + title + "/" + date + "<br />";

                WebElement url = content.findElement(By.cssSelector("td.td-subject > a"));
                result += "공지 url : " + url.getAttribute("href") + "<br />";
            }
        }

        driver.quit();

        System.out.println("####END####");

        return result;

    }

    @GetMapping("/page")
    public String seleniumPagging() {
        String result = "";

        System.out.println("####START####");

        Path path = Paths.get("D:\\chromedriver_win32\\chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", path.toString());

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-popup-blocking");   // 팝업 안띄움
        options.addArguments("headless");   // 브라우저 안띄움
        options.addArguments("--disable-gpu");  // gpu 비활성화
        options.addArguments("--blink-settings=imagesEnabled=false");   // 이미지 다운 안받음

        WebDriver driver = new ChromeDriver(options);

        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));    // 드라이버가 실행된 후 10초 기다림

        driver.get(URL);

//        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); // 페이지 전체가 로딩될때까지 기다림

        webDriverWait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("div._fnctWrap > form:nth-child(2) > div > table > tbody > tr"))
        );

        // 정규식(숫자 아닌 모든 문자 변경)을 이용해 마지막 페이지 구함
        String last = driver.findElement(By.cssSelector("div._fnctWrap > form:nth-child(3) > div > div > a._last")).getAttribute("href").replaceAll("[^0-9]", "");
        // 현재 페이지 구함
        String now = driver.findElement(By.cssSelector("div._fnctWrap > form:nth-child(3) > input#page")).getAttribute("value");

        while (!last.equals(now)) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>> " + now);
            List<WebElement> contents = driver.findElements(By.cssSelector("div._fnctWrap > form:nth-child(2) > div > table > tbody > tr"));
            if(contents.size() > 0) {
                for (WebElement content : contents) {
                    // 고정 공지는 크롤링 생략
//                    if (!content.getAttribute("class").equals("notice")) {
                        String title = content.findElement(By.cssSelector("td.td-subject > a > strong")).getText();
                        String date = content.findElement(By.cssSelector("td.td-date")).getText();
                        result += "공지 title / 작성일 : " + title + "/" + date + "<br />";

                        WebElement url = content.findElement(By.cssSelector("td.td-subject > a"));
                        result += "공지 url : " + url.getAttribute("href") + "<br />";
//                    }
                }
            }

            try {
                WebElement element = driver.findElement(By.cssSelector("a._listNext"));
                ((ChromeDriver) driver).executeScript("arguments[0].click();", element);
                System.out.println(driver.getCurrentUrl());

                webDriverWait.until(
                        ExpectedConditions.presenceOfElementLocated(By.cssSelector("div._fnctWrap > form:nth-child(2) > div > table > tbody > tr"))
                );
                now = driver.findElement(By.cssSelector("div._fnctWrap > form:nth-child(3) > input#page")).getAttribute("value");
            } catch (Exception e) {
                now = driver.findElement(By.cssSelector("div._fnctWrap > form:nth-child(3) > input#page")).getAttribute("value");
            }
        }

        if (last.equals(now)) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>> " + now);
            List<WebElement> contents = driver.findElements(By.cssSelector("div._fnctWrap > form:nth-child(2) > div > table > tbody > tr"));
            if(contents.size() > 0) {
                for (WebElement content : contents) {
                    // 고정 공지는 크롤링 생략
//                    if (!content.getAttribute("class").equals("notice")) {
                    String title = content.findElement(By.cssSelector("td.td-subject > a > strong")).getText();
                    String date = content.findElement(By.cssSelector("td.td-date")).getText();
                    result += "공지 title / 작성일 : " + title + "/" + date + "<br />";

                    WebElement url = content.findElement(By.cssSelector("td.td-subject > a"));
                    result += "공지 url : " + url.getAttribute("href") + "<br />";
//                    }
                }
            }
        }

        driver.quit();

        System.out.println("####END####");

        return result;

    }


    @GetMapping("/login")
    public String login() throws InterruptedException {
        String result = "";

        System.out.println("####START####");

        Path path = Paths.get("D:\\chromedriver_win32\\chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", path.toString());

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-popup-blocking");   // 팝업 안띄움
        options.addArguments("headless");   // 브라우저 안띄움
        options.addArguments("--disable-gpu");  // gpu 비활성화
        options.addArguments("--blink-settings=imagesEnabled=false");   // 이미지 다운 안받음

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));    // 드라이버가 실행된 후 10초 기다림

        driver.get(Login_URL);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); // 페이지 전체가 로딩될때까지 기다림

        WebElement webElement = driver.findElement(By.id("userId"));
        webElement.sendKeys(user_id);

        webElement = driver.findElement(By.id("password"));
        webElement.sendKeys(user_pwd);

        webElement = driver.findElement(By.className("btn_login"));
        webElement.click();

//        Thread.sleep(3000);

        // 로그인 성공 후 포털에서 주간주요일정 가져오기
        System.out.println(driver.getCurrentUrl());

        webDriverWait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("#scListUl > li"))
        );

        List<WebElement> contents = driver.findElements(By.cssSelector("#scListUl > li"));
        if(contents.size() > 0) {
            for (WebElement content : contents) {
                String date = content.findElement(By.cssSelector("span")).getText();
                WebElement cont = content.findElement(By.cssSelector("a"));

                result += "일정명 : " + cont.getAttribute("title") + "<br />";
                result += "일정 : " + date + "<br />";    // 스크롤 다운 추가 필요
                result += "url : " + cont.getAttribute("href") + "<br />";
                result += "============================<br />";
            }
        }

        driver.quit();

        System.out.println("####END####");

        return result;

    }

    /*
     * 버튼 클릭 시 새탭으로 페이지가 열리는 경우
     */
    @GetMapping("/new_tab")
    public String newTab() throws InterruptedException {
        String result = "";

        System.out.println("####START####");

        Path path = Paths.get("D:\\chromedriver_win32\\chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", path.toString());

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-popup-blocking");   // 팝업 안띄움
        options.addArguments("headless");   // 브라우저 안띄움
        options.addArguments("--disable-gpu");  // gpu 비활성화
        options.addArguments("--blink-settings=imagesEnabled=false");   // 이미지 다운 안받음

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));    // 드라이버가 실행된 후 10초 기다림

        driver.get("https://www.saramin.co.kr/zf_user/company-search?searchWord=%ED%98%84%EB%8C%80%EB%AA%A8%EB%B9%84%EC%8A%A4");

        webDriverWait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.wrap_company_search > div > div.list_company_search > div.wrap_list > div:nth-child(1) > a"))
        );

        WebElement webElement = driver.findElement(By.cssSelector("div.wrap_company_search > div > div.list_company_search > div.wrap_list > div:nth-child(1) > a"));
        webElement.click();

        String newTabHandle = driver.getWindowHandles().toArray()[1].toString();
        driver.switchTo().window(newTabHandle);

        webDriverWait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.header_company_view > div.title_company_view > h1 > span.name"))
        );

        String content = driver.findElement(By.cssSelector("div.header_company_view > div.title_company_view > h1 > span.name")).getText();
        result = content;

        driver.quit();

        System.out.println("####END####");

        return result;

    }
}
