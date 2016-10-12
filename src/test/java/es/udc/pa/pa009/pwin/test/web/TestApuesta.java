package es.udc.pa.pa009.pwin.test.web;

import java.util.Calendar;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

import org.junit.*;

import static es.udc.pa.pa009.pwin.model.util.GlobalNames.SPRING_CONFIG_FILE;
import static es.udc.pa.pa009.pwin.test.util.GlobalNames.SPRING_CONFIG_TEST_FILE;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import es.udc.pa.pa009.pwin.model.adminservice.AdminService;
import es.udc.pa.pa009.pwin.model.apuesta.Opcion;
import es.udc.pa.pa009.pwin.model.apuesta.OpcionDao;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuesta;
import es.udc.pa.pa009.pwin.model.apuesta.TipoApuestaDao;
import es.udc.pa.pa009.pwin.model.evento.Categoria;
import es.udc.pa.pa009.pwin.model.evento.CategoriaDao;
import es.udc.pa.pa009.pwin.model.evento.Evento;
import es.udc.pa.pa009.pwin.model.evento.EventoDao;
import es.udc.pa.pa009.pwin.model.userprofile.UserProfile;
import es.udc.pa.pa009.pwin.model.userservice.UserProfileDetails;
import es.udc.pa.pa009.pwin.model.userservice.UserService;



public class TestApuesta {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  @Autowired
  private AdminService adminService;
  @Autowired
  private UserService userService;
  @Autowired
  private EventoDao eventoDao;
  @Autowired
  private TipoApuestaDao tipoApuestaDao;
  @Autowired
  private OpcionDao opcionDao;
  
  
  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "http://localhost:8080";
    
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testApuesta() throws Exception {
	  driver.get(baseUrl + "/pwin/");
	    driver.findElement(By.linkText("Autenticarse")).click();
	    driver.findElement(By.id("loginName")).clear();
	    driver.findElement(By.id("loginName")).sendKeys("paco");
	    driver.findElement(By.id("password")).clear();
	    driver.findElement(By.id("password")).sendKeys("paco");
	    driver.findElement(By.cssSelector("button.btn.btn-primary")).click();
	    driver.findElement(By.id("keywordsField")).clear();
	    driver.findElement(By.id("keywordsField")).sendKeys("eve");
	    driver.findElement(By.cssSelector("button.btn.btn-default")).click();
	    driver.findElement(By.linkText("evento1")).click();
	    driver.findElement(By.linkText("op1")).click();
	    driver.findElement(By.linkText("Apostar por esta opcion")).click();
	    driver.findElement(By.id("cantidad")).clear();
	    driver.findElement(By.id("cantidad")).sendKeys("1842000");
	    driver.findElement(By.id("submit_1")).click();
	    driver.findElement(By.linkText("paco")).click();
	    driver.findElement(By.linkText("Sair")).click();
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}
