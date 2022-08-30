package com.example.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

@RestController
public class SeleniumController {

    private String URL = "https://cse.inhatc.ac.kr/cse/2206/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGY3NlJTJGMTA3JTJGYXJ0Y2xMaXN0LmRvJTNG";

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
                //cssSelector로 선택한 부분이 로딩횔때까지 기다림
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
}