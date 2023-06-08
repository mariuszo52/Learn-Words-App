package pl.languagelearn.application.user;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.languagelearn.application.userRole.UserRoleDto;
import pl.languagelearn.application.userRole.UserRoleService;

import java.util.List;
import java.util.Set;

@Controller
class UserController {
private final UserService userService;
private final UserRoleService userRoleService;

    UserController(UserService userService, UserRoleService userRoleService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
    }

    @GetMapping("/stats")
    String getUserStats(Model model){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.findUserStatsByEmail(username).ifPresentOrElse(
                userDto -> model.addAttribute("user", userDto),
                () -> {
                  throw new RuntimeException("Nie znaleziono użytkownika o podanej nazwie");
                });
        return "stats";

    }
    @GetMapping("/admin-panel")
    String getAdminPanel(Model model){
        List<UserDto> allUsers = userService.findAllUsers();
        model.addAttribute("users", allUsers);
        return "admin-panel";
    }

    @DeleteMapping("/admin-panel")
    String deleteUser(@RequestParam long userId, RedirectAttributes redirectAttributes){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto userDto = userService.findUserByEmail(username).orElseThrow();
        if(userId != userDto.getId()) {
            userService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("message", "Usunięto użytkownika");
        }
        else{
            redirectAttributes.addFlashAttribute("message", "Nie udało się usunąć użytkownika");
        }
        return "redirect:/admin-panel";

    }
    @GetMapping("/admin-panel/user-edit/{id}")
    String editUser(@PathVariable long id, Model model){
        UserEditDto userEditDto = new UserEditDto();
        model.addAttribute("user", userEditDto);
        Set<UserRoleDto> allRoles = userRoleService.getAllRoles();
        model.addAttribute("allRoles", allRoles);
        model.addAttribute("Id", id);
        return "user-edit";
    }
    @PatchMapping("/admin-panel/block")
    String blockUser(@RequestParam long userId, RedirectAttributes redirectAttributes){
        if(userId != UserService.getLoggedUserId()) {
            userService.blockUser(userId);
            redirectAttributes.addFlashAttribute("message", "Zablokowano użytkownika");
        }
        else {
            redirectAttributes.addFlashAttribute("message", "Nie udało się zablokować użytkownika.");
        }
        return "redirect:/admin-panel";
    }
    @PatchMapping("/admin-panel/unblock")
    String unblockUser(@RequestParam long userId, RedirectAttributes redirectAttributes){
        userService.unblockUser(userId);
        redirectAttributes.addFlashAttribute("message", "Odblokowano konto użytkownika.");
        return "redirect:/admin-panel";
    }


    @PatchMapping("/admin-panel/user-edit/{id}")
    String updateUserData(@PathVariable long id, UserEditDto userEditDto, RedirectAttributes redirectAttributes){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto userDto = userService.findUserByEmail(username).orElseThrow();
        if(!(userDto.getId() == id)){
        userEditDto.setId(id);
        userService.updateUser(userEditDto);
        redirectAttributes.addFlashAttribute("message", "Pomyślnie zaktualizowano użytkownika");
        }
        else {
            redirectAttributes.addFlashAttribute("message",
                    "Nie można edytować użytkownika, na którym jest się zalogowanym");
        }
        return "redirect:/admin-panel";

    }

    @GetMapping("/user-panel")
    String getUserPanel(){
        return "user-panel";
    }

    @DeleteMapping("/user-panel/delete")
    String deleteUser(HttpServletRequest request, RedirectAttributes redirectAttributes){
        try{
            userService.deleteUser(UserService.getLoggedUserId());
            request.getSession().invalidate();
            redirectAttributes.addFlashAttribute("message", "Twoje konto zostało usunięte.");
            return "redirect:/";
        }catch (RuntimeException e){
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/user-panel";
        }
    }
    @DeleteMapping("/user-panel/reset-words")
    String resetWords(RedirectAttributes redirectAttributes){
        try{
            userService.deleteAllUserWords();
            redirectAttributes.addFlashAttribute("message", "Zresetowano słówka.");
        }catch (RuntimeException e){
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/user-panel";
    }
    @PatchMapping("/user-panel/reset-stats")
    String resetStats(RedirectAttributes redirectAttributes){
    try{
        userService.resetStats();
        redirectAttributes.addFlashAttribute("message", "Zresetowano statystyki.");
    }catch (RuntimeException e){
        redirectAttributes.addFlashAttribute("message", e.getMessage());
    }
    return "redirect:/user-panel";
    }
    @PatchMapping("/user-panel/change-password")
    String changePassword(@RequestParam String oldPass, @RequestParam String newPass, RedirectAttributes redirectAttributes){
        try{
            userService.changePassword(oldPass, newPass);
            redirectAttributes.addFlashAttribute("message", "Zmienieno hasło.");
        }catch (RuntimeException e){
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/user-panel";
    }
}


