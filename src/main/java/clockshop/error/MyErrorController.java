package clockshop.error;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyErrorController implements org.springframework.boot.web.servlet.error.ErrorController{

	private static final String ERROR = "error";

	/**
	 * Show error message
	 * Eventually log error data
	 */

		@GetMapping("/error")
		public String handleError(HttpServletResponse response, Model model) {
			Integer status = response.getStatus();
			model.addAttribute("errorCode", status);
			String errorMessage = switch (status) {
                case 400 -> "Bad Request";
                case 401 -> "Unauthorized";
                case 403 -> "Forbidden";
                case 404 -> "Not Found";
                case 405 -> "Method Not Allowed";
                case 408 -> "Request Timeout";
                case 415 -> "Unsupported Media File";
                case 500 -> "Internal Server Message";
                default -> "Unknown Error Occurred";
            };
            model.addAttribute("errorString", errorMessage);
			return ERROR;
		}

}
