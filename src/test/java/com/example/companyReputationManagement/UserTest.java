package com.example.companyReputationManagement;

import com.example.companyReputationManagement.dao.UserDao;
import com.example.companyReputationManagement.dto.user.create.UserCreateRequestDTO;
import com.example.companyReputationManagement.dto.user.create.UserCreateResponseDTO;
import com.example.companyReputationManagement.dto.user.edit.EditUserRequestDTO;
import com.example.companyReputationManagement.dto.user.edit.EditUserResponseDTO;
import com.example.companyReputationManagement.dto.user.get_by_code.GetUserByCodeResponseDTO;
import com.example.companyReputationManagement.dto.user.login.UserLoginRequestDTO;
import com.example.companyReputationManagement.dto.user.login.UserLoginResponseDTO;
import com.example.companyReputationManagement.factories.UserFactory;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.IJwtService;
import com.example.companyReputationManagement.iservice.generate.GenerateCode;
import com.example.companyReputationManagement.iservice.services.users.UserService;
import com.example.companyReputationManagement.mapper.UserMapper;
import com.example.companyReputationManagement.models.CompanyUser;
import com.example.companyReputationManagement.models.enums.RoleEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.util.ArrayList;
import java.util.List;

import static com.example.companyReputationManagement.constants.SysConst.OC_BUGS;
import static com.example.companyReputationManagement.constants.SysConst.OC_OK;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserDao userDao;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private GenerateCode generateCode;
    @Mock
    private IJwtService tokenService;

    @Test
    void testLogin_success() {
        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(
                "test", "test"
        );
        CompanyUser companyUser = UserFactory.defaultUser();

        final List<OAuth2AccessToken> accessTokens = new ArrayList<>();

        accessTokens.add(new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "dsafdsgfhd", null, null));
        accessTokens.add(new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "dasgdsafd", null, null));


        when(userDao.findUserByLoginOrEmail(userLoginRequestDTO.getUsernameOrEmail())).thenReturn(companyUser);
        when(passwordEncoder.matches(userLoginRequestDTO.getPassword(), companyUser.getPasswordHash())).thenReturn(true);
        when(tokenService.createTokens("code", "test", RoleEnum.USER)).thenReturn(accessTokens);
        HttpResponseBody<UserLoginResponseDTO> result = userService.login(userLoginRequestDTO);
        assertEquals("User logged in successfully", result.getMessage());

    }

    @Test
    void testLogin_IncorrectPassword() {
        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(
                "test", "test"
        );
        CompanyUser companyUser = UserFactory.defaultUser();
        when(userDao.findUserByLoginOrEmail(userLoginRequestDTO.getUsernameOrEmail())).thenReturn(companyUser);
        when(passwordEncoder.matches(userLoginRequestDTO.getPassword(), companyUser.getPasswordHash())).thenReturn(false);
        HttpResponseBody<UserLoginResponseDTO> result = userService.login(userLoginRequestDTO);
        assertEquals("Incorrect password", result.getMessage());

    }

    @Test
    void testLogin_UserNotFound() {
        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(
                "test", "test"
        );
        when(userDao.findUserByLoginOrEmail(userLoginRequestDTO.getUsernameOrEmail())).thenReturn(null);

        HttpResponseBody<UserLoginResponseDTO> result = userService.login(userLoginRequestDTO);
        assertEquals("User not found", result.getMessage());

    }

    @Test
    void testRegister_success() {
        UserCreateRequestDTO request = UserFactory.validCreateRequest();

        CompanyUser user = UserFactory.defaultUser();

        when(userDao.findUserByLoginOrEmail(request.getUsername(), request.getEmail())).thenReturn(null);
        when(userMapper.mapUserDtoToUser(request)).thenReturn(user);
        when(userDao.save(user)).thenReturn(user);
        when(userMapper.mapUserToUserDtoResponse(user)).thenReturn(new UserCreateResponseDTO("email@example.com", "test"));

        HttpResponseBody<UserCreateResponseDTO> response = userService.register(request);

        assertEquals("User registered successfully", response.getMessage());
        assertEquals("test", response.getResponseEntity().getUsername());
        assertTrue(response.getErrors().isEmpty());
        assertEquals(OC_OK, response.getResponseCode());
    }

    @Test
    void testRegister_saveFails() {
        UserCreateRequestDTO request = UserFactory.validCreateRequest();

        when(userDao.findUserByLoginOrEmail(request.getUsername(), request.getEmail())).thenReturn(null);
        when(userMapper.mapUserDtoToUser(request)).thenReturn(null);

        HttpResponseBody<UserCreateResponseDTO> response = userService.register(request);

        assertEquals("User registration failed", response.getMessage());
        assertNull(response.getResponseEntity());
        assertEquals(OC_BUGS, response.getResponseCode());
        assertEquals("User not saved", response.getErrors().getFirst().getErrorMsg());
    }

    @Test
    void testRegister_loginIsExists() {
        UserCreateRequestDTO request = UserFactory.validCreateRequest();
        CompanyUser user = UserFactory.defaultUser();
        when(userDao.findUserByLoginOrEmail(request.getUsername(), request.getEmail())).thenReturn(user);

        HttpResponseBody<UserCreateResponseDTO> response = userService.register(request);

        assertEquals("Username is already taken", response.getMessage());

        assertEquals(OC_BUGS, response.getResponseCode());
        assertEquals("User exists", response.getErrors().getFirst().getErrorMsg());
    }

    @Test
    void testRegister_emailIsExists() {
        UserCreateRequestDTO request = UserFactory.validCreateRequest();
        CompanyUser user = UserFactory.defaultUser();
        user.setUsername("123");
        when(userDao.findUserByLoginOrEmail(request.getUsername(), request.getEmail())).thenReturn(user);

        HttpResponseBody<UserCreateResponseDTO> response = userService.register(request);

        assertEquals("Email is already taken", response.getMessage());

        assertEquals(OC_BUGS, response.getResponseCode());
        assertEquals("User exists", response.getErrors().getFirst().getErrorMsg());
    }

    @Test
    void testEdit_usernameIsTaken() {
        EditUserRequestDTO request = UserFactory.editRequest();
        CompanyUser user = UserFactory.defaultUser();
        user.setUsername(request.getNewUsername());
        when(userDao.findUserByLoginOrEmail(request.getNewUsername(), request.getNewEmail())).thenReturn(user);

        HttpResponseBody<EditUserResponseDTO> response = userService.edit(request);

        assertEquals("Username is already taken.", response.getMessage());
        assertEquals("Username is already taken.", response.getError());
        assertEquals(OC_BUGS, response.getResponseCode());
        assertNull(response.getResponseEntity());
    }

    @Test
    void testEdit_emailIsTaken() {
        EditUserRequestDTO request = UserFactory.editRequest();
        request.setNewEmail("taken@example.com");
        request.setNewUsername("takenUsername");
        CompanyUser user = UserFactory.defaultUser();
        user.setUsername("dsadsad");
        user.setEmail("taken@example.com");
        when(userDao.findUserByLoginOrEmail(request.getNewUsername(), request.getNewEmail())).thenReturn(user);

        HttpResponseBody<EditUserResponseDTO> response = userService.edit(request);

        assertEquals("Email is already taken.", response.getMessage());
        assertEquals("Email is already taken.", response.getError());
        assertEquals(OC_BUGS, response.getResponseCode());
        assertNull(response.getResponseEntity());
    }

    @Test
    void testEdit_bothUsernameAndEmailAreTaken() {
        EditUserRequestDTO request = UserFactory.editRequest();
        CompanyUser user = UserFactory.defaultUser();
        user.setUsername(request.getNewUsername());
        user.setEmail(request.getNewEmail());

        when(userDao.findUserByLoginOrEmail(request.getNewUsername(), request.getNewEmail())).thenReturn(user);

        HttpResponseBody<EditUserResponseDTO> response = userService.edit(request);

        assertEquals("Username is already taken. Email is already taken.", response.getMessage());
        assertEquals("Username is already taken. Email is already taken.", response.getError());
        assertEquals(OC_BUGS, response.getResponseCode());
        assertNull(response.getResponseEntity());
    }

    @Test
    void testEdit_userNotFound() {
        EditUserRequestDTO request = UserFactory.editRequest();


        when(tokenService.extractUserCodeFromJwt()).thenReturn("user-code");
        when(userDao.findUserByUserCode("user-code")).thenReturn(null);

        HttpResponseBody<EditUserResponseDTO> response = userService.edit(request);

        assertEquals("User not found", response.getMessage());
        assertEquals("User not found", response.getError());
        assertEquals(OC_BUGS, response.getResponseCode());
        assertNull(response.getResponseEntity());
    }

    @Test
    void testEdit_success() {
        EditUserRequestDTO request = UserFactory.editRequest();


        CompanyUser existingUser = UserFactory.defaultUser();
        CompanyUser updatedUser = UserFactory.defaultUser();
        EditUserResponseDTO responseDTO = UserFactory.editResponse();

        when(userDao.findUserByLoginOrEmail(request.getNewUsername(), request.getNewEmail())).thenReturn(null);
        when(tokenService.extractUserCodeFromJwt()).thenReturn("user-code");
        when(userDao.findUserByUserCode("user-code")).thenReturn(existingUser);
        when(userMapper.mapUserDtoToUser(request, existingUser)).thenReturn(updatedUser);
        when(userDao.save(updatedUser)).thenReturn(updatedUser);
        when(userMapper.mapUserToUserResponse(updatedUser)).thenReturn(responseDTO);

        HttpResponseBody<EditUserResponseDTO> response = userService.edit(request);

        assertEquals("User updated successfully", response.getMessage());
        assertEquals(OC_OK, response.getResponseCode());
        assertEquals(responseDTO, response.getResponseEntity());
        assertTrue(response.getErrors().isEmpty());
    }

    @Test
    void testGetUserByCode_unauthorized() {
        when(tokenService.extractUserCodeFromJwt()).thenReturn(null);

        HttpResponseBody<GetUserByCodeResponseDTO> response = userService.getUserByCode();

        assertEquals("User unauthorized", response.getMessage());
        assertEquals(OC_BUGS, response.getResponseCode());
        assertNull(response.getResponseEntity());
    }

    @Test
    void testGetUserByCode_userNotFound() {
        String userCode = "someUserCode";
        when(tokenService.extractUserCodeFromJwt()).thenReturn(userCode);
        when(userDao.findUserByUserCode(userCode)).thenReturn(null);  // Пользователь не найден


        HttpResponseBody<GetUserByCodeResponseDTO> response = userService.getUserByCode();

        assertEquals("User not found", response.getMessage());
        assertEquals("User not found", response.getError());
        assertEquals(OC_BUGS, response.getResponseCode());
        assertNull(response.getResponseEntity());
    }

    @Test
    void testGetUserByCode_userFound() {

        String userCode = "someUserCode";
        CompanyUser mockUser = UserFactory.defaultUser();
        GetUserByCodeResponseDTO responseDTO = UserFactory.getUserResponse();

        when(tokenService.extractUserCodeFromJwt()).thenReturn(userCode);
        when(userDao.findUserByUserCode(userCode)).thenReturn(mockUser);
        when(userMapper.mapUserToUserDto(mockUser)).thenReturn(responseDTO);

        HttpResponseBody<GetUserByCodeResponseDTO> response = userService.getUserByCode();
        assertEquals("User found successfully", response.getMessage());
        assertEquals(OC_OK, response.getResponseCode());
        assertNotNull(response.getResponseEntity());
    }


    @Test
    void testUserMapper() {
        CompanyUser user = UserFactory.defaultUser();
        UserMapper userMapper1 = new UserMapper(passwordEncoder, generateCode);
        UserCreateResponseDTO userDto = userMapper1.mapUserToUserDtoResponse(user);
        assertEquals(user.getUsername(), userDto.getUsername());
    }

}
