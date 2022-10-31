package application.demo.controller;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import application.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

//import application.demo.exception.UserNotFoundException;
import application.demo.model.User;
import application.demo.repository.UserRepository;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/users")
public class UserController {
	int numberPerPage = 10;
	Map<Integer, List<User>> userPerPage = new HashMap<>();

	@Autowired
	private UserService userService;

	public int countPages(int numberPerPage){
		if(userService.findAllUsers().size() % numberPerPage != 0){
			return userService.findAllUsers().size() / numberPerPage +1;
		}
		return userService.findAllUsers().size() / numberPerPage;
	}
	public List<Integer> pageList(int pages){
		List<Integer> myList = IntStream.range(1, pages + 1).boxed()
				.collect(Collectors.toList());

		return myList;
	}


//	@GetMapping("/all")
//	public List<User> listAll(){
//		return (List<User>) userService.findAll();
//	}
	@GetMapping("register")
	public String employeeForm() {

		return "index";
	  }

	//add mapping for "/list"
	@GetMapping({"/list"})
	public String userList(Model theModel) {

		//TODO: Put users list into Map, each key is the total users of each page.
		List<User> users = new ArrayList<>();
		users = userService.findAllUsers();

		int maxUsers = users.size();
		int maxPage = (int)Math.floor(maxUsers / numberPerPage) + 1;
		int extraUser = maxUsers % numberPerPage;

		if(maxUsers < numberPerPage){
			userPerPage.put(1, users.subList(0, extraUser));
		} else{
			for (int i = 0; i < maxPage-1; i++) {
				userPerPage.put(i+1, users.subList(i * numberPerPage, i * numberPerPage + numberPerPage));
			}
			if (extraUser > 0) {
				userPerPage.put(maxPage,
						users.subList((maxPage - 1) * numberPerPage, (maxPage - 1) * numberPerPage + extraUser));
			}
		}

		theModel.addAttribute("previousPage", 0);
		theModel.addAttribute("nextPage", 2);
		theModel.addAttribute("users", userPerPage.get(1));
		theModel.addAttribute("pages", pageList(countPages(numberPerPage)));
		return "list-user";
	}

	@GetMapping({"/list/{page}"})
	public String userList(@PathVariable Integer page, Model theModel) {
		List<User> users = userService.findAllUsers();
		int maxUsers = users.size();
		int maxPage = (int)Math.floor(maxUsers / numberPerPage) + 1;

		int nextPage = page + 1;
		int previousPage = page - 1;
		theModel.addAttribute("nextMaxPage", maxPage +1);
		theModel.addAttribute("nextPage", nextPage);
		theModel.addAttribute("previousPage", previousPage);
		theModel.addAttribute("users", userPerPage.get(page));
		theModel.addAttribute("pages", pageList(countPages(numberPerPage)));
		return "list-user";
	}

	@GetMapping("/import")
	public String importUser(Model theModel) {
		User user;
		for(Integer i=0; i< 98; i++){
			user = new User( i, generateStringName(), 	"abn", "xuan@gmail.com", (int)(Math.random() * 100) +1);
			userService.save(user);
		}
		return "welcome";
	}

	@PostMapping("/register")
	public String userRegistration(@ModelAttribute User user, Model model) {
		System.out.println(user.toString());
		System.out.println(user.getFirstName());
		System.out.println(user.getLastName());
		System.out.println(user.getEmail());

//		User user_inserted = userService.findUserById(user.getId());
		
//		User user_existed = userRepository.find(user.getId());
		
		userService.save(user);
		model.addAttribute("message", user.getEmail() + " inserted");
		return "welcome";
	}
	 
	 //using path variable
	 @GetMapping("/edit/{id}")
	  public String employeeGetFormById(@PathVariable Integer id, Model model) {

		 // TODO: check if user existed then update info else throw not found
//		 boolean user_existed = userService.get(id);
//		 if (!user_existed) {
//			 // TODO: throw new Error
//			 System.out.println("User Not Found");
//			 return "user-not-found";
//		 }
		User user = userService.get(id);
		model.addAttribute("user", user);
	    return "form-edit";
	  }

	 
	 @PostMapping("/edit/{id}")
	 public String userSubmitFormEdit(@PathVariable Integer id, @ModelAttribute("user") User user, Model model) {
		 
		System.out.println(id);

		// TODO: save user with new information
		 //

		User user_inserted = userService.findUserById(user.getId());
		userService.save(user);
		
		// TODO: .....
		model.addAttribute("message", user_inserted.getEmail() + " modified");
		return "welcome";
	  }

	  @PostMapping("/delete/{id}")
	public ModelAndView deleteUserById(@PathVariable Integer id, ModelMap model){
		  userService.delete(id);
		return new ModelAndView("redirect:/users/list", model);
	  }
//	  @PostMapping
//	public String deleteById(@PathVariable Integer id, Model model){
//		  userRepository.deleteById(id);
//		  return userList(model);
//	  }


	public String generateStringName() {
		int leftLimit = 48; // numeral '0'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 10;
		Random random = new Random();

		String generatedString = random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
				.limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();

		return generatedString;
	}

}
