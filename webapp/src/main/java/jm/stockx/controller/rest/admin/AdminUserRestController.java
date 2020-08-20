package jm.stockx.controller.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.stockx.UserService;
import jm.stockx.dto.UserDto;
import jm.stockx.dto.UserPutDto;
import jm.stockx.entity.User;
import jm.stockx.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping(value = "/rest/api/admin")
@Tag(name = "admin", description = "Admin API")
@Slf4j
public class AdminUserRestController {
    private final UserService userService;

    public AdminUserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(
            operationId = "getAllUsers",
            summary = "Get all users",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = User.class)
                            ),
                            description = "OK: user list received"
                    ),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND: no users found")
            })
    public Response<List<UserDto>> getAllUsers() {
        List<UserDto> userDtos = new LinkedList<>();
        List<User> users = userService.getAllUsers();
        for (User user : users
        ) {
            UserDto userDto = new UserDto(user);
            userDtos.add(userDto);
        }
        log.info("Получен список пользователей. Всего {} записей.", userDtos.size());
        return Response.ok(userDtos);
    }

    @PostMapping(value = "/createUser")
    @Operation(
            operationId = "createUser",
            summary = "Create user",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: user created"),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND: user not created")
            })
    public Response<?> createUser(@Valid @RequestBody User user) {
        String username = user.getUsername();
        if (userService.isUserExist(user.getId())) {
            userService.createUser(user);
            log.info("Пользователь {} успешно создан", username);
            return Response.ok(HttpStatus.OK, "User created successfully");
        }
        log.warn("Пользователь {} уже существует в базе", username);
        return Response.error(HttpStatus.BAD_REQUEST, "Error when creating a user");
    }

    @PutMapping(value = "/updateUser")
    @Operation(
            operationId = "updateUser",
            summary = "Update user",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: user updated successfully"),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND: unable to update user")
            })
    public Response<?> updateUser(@Valid @RequestBody UserPutDto userPutDto) {
        String username = userPutDto.getUsername();
        if (userService.isUserExist(userPutDto.getId())) {
            User userUpdate = new User(userPutDto.getId(), userPutDto.getFirstName(), userPutDto.getLastName(),
                                userPutDto.getEmail(), userPutDto.getUsername(), userPutDto.getPassword(),
                                userPutDto.getSellerLevel());
            userService.updateUser(userUpdate);
            log.info("Пользователь {} успешно обновлен", username);
            return Response.ok().build();
        }
        log.warn("Пользователь {} в базе не найден", username);
        return Response.error(HttpStatus.BAD_REQUEST, "User not found");
    }

    @DeleteMapping(value = "/deleteUser")
    @Operation(
            operationId = "deleteUser",
            summary = "Delete user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: user deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "NOT FOUND: no user with such id")
            }
    )
    public Response<Boolean> deleteUser(@PathVariable Long id) {
        if (userService.isUserExist(id)) {
            userService.deleteUser(id);
            log.info("Пользователь с id = {} успешно удален", id);
            return Response.ok().build();
        }
        log.warn("Пользователь с id = {} в базе не найден", id);
        return Response.error(HttpStatus.BAD_REQUEST, "User not found");
    }

    @PostMapping(value = "/addUserImage")
    public Response<?> addUserImage(@RequestParam MultipartFile file, @RequestParam Long id) {
        User user = userService.getUserById(id);
        String fileName = file.getOriginalFilename();
        String uploadRootPath = AdminUserRestController.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        user.setImageUrl(uploadRootPath + user.getUsername());
        userService.updateUser(user);
        File uploadRootDir = new File(uploadRootPath + user.getUsername());
        if (!uploadRootDir.exists()) {
            uploadRootDir.mkdirs();
        }
        if (fileName != null && fileName.length() > 0) {
            try {
                File serverFile = new File(uploadRootDir.getAbsolutePath() + File.separator + fileName);
                try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile))) {
                    stream.write(file.getBytes());
                }
            } catch (Exception e) {
                log.warn("Картинка не загружена");
                return Response.error(HttpStatus.BAD_REQUEST, "Image not loaded");
            }
        }
        return Response.ok().build();
    }
}
