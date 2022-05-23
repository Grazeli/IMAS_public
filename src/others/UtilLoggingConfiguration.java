package others;
import java.io.InputStream;
import java.sql.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class UtilLoggingConfiguration {
  private static Logger LOGGER = null;

  static {
      Logger mainLogger = Logger.getLogger(UtilLoggingConfiguration.class.getName());
      mainLogger.setUseParentHandlers(false);
      ConsoleHandler handler = new ConsoleHandler();
      handler.setFormatter(new SimpleFormatter() {
          private static final String format = "%3$s %n";

          @Override
          public synchronized String format(LogRecord lr) {
              return String.format(format,
                      new Date(lr.getMillis()),
                      lr.getLevel().getLocalizedName(),
                      lr.getMessage()
              );
          }
      });
      mainLogger.addHandler(handler);
      LOGGER = Logger.getLogger(UtilLoggingConfiguration.class.getName());
      LOGGER.setLevel(Level.OFF);
  }
  
  public void setLevel(Level newLevel) {
	LOGGER.setLevel(newLevel);
  }
  
  public void info(String text) {
	  LOGGER.log(Level.INFO, text);
  }
  
  public void warning(String text) {
	  LOGGER.log(Level.WARNING, text);
  }
  
  public void severe(String text) {
	  LOGGER.log(Level.SEVERE, text);
  }

  public void config(String text) {
	  LOGGER.log(Level.CONFIG, text);	
  }
}