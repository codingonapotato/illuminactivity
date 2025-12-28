import static org.junit.jupiter.api.Assertions.assertEquals;
import io.github.codingonapotato.Illuminactivity;
import org.junit.jupiter.api.Test;

class IlluminactivityTest {

	private final Illuminactivity controller = new Illuminactivity();

	@Test
	void consoleLog() {
		String myStr = "Hello world!";
		assertEquals("Hello world!", myStr);
	}

}