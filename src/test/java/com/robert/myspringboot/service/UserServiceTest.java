package com.robert.myspringboot.service;

import com.robert.myspringboot.dao.FakeDataDao;
import com.robert.myspringboot.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

public class UserServiceTest {

    @Mock
    private FakeDataDao fakeDataDao;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userService = new UserService(fakeDataDao);
    }

    @Test
    void shouldGetAllUsers() {
        UUID annaUserUid = UUID.randomUUID();

        User anna = new User(annaUserUid, "anna", "montana",
                User.Gender.FEMALE, 30, "anna@gmail.com");

        List<User> users = new ArrayList<>(Arrays.asList());
        users.add(anna);
        List<User> unmodifiableList = Collections.unmodifiableList(users);

        given(fakeDataDao.selectAllUsers()).willReturn(unmodifiableList);

        List<User> allUsers = userService.getAllUsers(Optional.empty());

        assertThat(allUsers).hasSize(1);

        User user = allUsers.get(0);

        assertAnnaFields(user);
    }

    @Test
    void shouldGetAllUsersByGender() {
        UUID annaUserUid = UUID.randomUUID();

        User anna = new User(annaUserUid, "anna", "montana",
                User.Gender.FEMALE, 30, "anna@gmail.com");

        UUID joeUserUid = UUID.randomUUID();

        User joe = new User(joeUserUid, "joe", "jones",
                User.Gender.MALE, 30, "joe.jones@gmail.com");

        List<User> users = new ArrayList<>(Arrays.asList());
        users.add(anna);
        users.add(joe);
        List<User> unmodifiableList = Collections.unmodifiableList(users);

        given(fakeDataDao.selectAllUsers()).willReturn(unmodifiableList);

        List<User> filteredUsers = userService.getAllUsers(Optional.of("female"));
        assertThat(filteredUsers).hasSize(1);
        assertAnnaFields(filteredUsers.get(0));

    }

    @Test
    void shouldThrowExceptionWhenGenderIsInvalid() {
        assertThatThrownBy(()-> userService.getAllUsers(Optional.of("asdfasdf")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid gender");
    }

    @Test
    void shouldGetUser() {
        UUID annaUid = UUID.randomUUID();
        User anna = new User(annaUid, "anna", "montana",
                User.Gender.FEMALE, 30, "anna@gmail.com");

        given(fakeDataDao.selectUserByUserUid(annaUid)).willReturn(Optional.of(anna));

        Optional<User> userOptional = userService.getUser(annaUid);

        assertThat(userOptional.isPresent()).isTrue();
        User user = userOptional.get();

        assertAnnaFields(user);

    }

    @Test
    void shouldUpdateUser() {
        UUID annaUid = UUID.randomUUID();
        User anna = new User(annaUid, "anna", "montana",
                User.Gender.FEMALE, 30, "anna@gmail.com");

        given(fakeDataDao.selectUserByUserUid(annaUid)).willReturn(Optional.of(anna));
        given(fakeDataDao.updateUser(anna)).willReturn(1);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        int updateResult = userService.updateUser(anna);

        verify(fakeDataDao).selectUserByUserUid(annaUid);
        verify(fakeDataDao).updateUser(captor.capture());

        User user = captor.getValue();
        assertAnnaFields(user);

        assertThat(updateResult).isEqualTo(1);

    }

    @Test
    void shouldRemoveUser() {
        UUID annaUid = UUID.randomUUID();
        User anna = new User(annaUid, "anna", "montana",
                User.Gender.FEMALE, 30, "anna@gmail.com");

        given(fakeDataDao.selectUserByUserUid(annaUid)).willReturn(Optional.of(anna));
        given(fakeDataDao.deleteUserByUserUid(annaUid)).willReturn(1);

        int deleteResult = userService.removeUser(annaUid);

        verify(fakeDataDao).selectUserByUserUid(annaUid);
        verify(fakeDataDao).deleteUserByUserUid(annaUid);

        assertThat(deleteResult).isEqualTo(1);

    }

    @Test
    void shouldInsertUser() {
        User anna = new User(null, "anna", "montana",
                User.Gender.FEMALE, 30, "anna@gmail.com");

        given(fakeDataDao.insertUser(any(UUID.class), eq(anna))).willReturn(1);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        int insertResult = userService.insertUser(anna);

        verify(fakeDataDao).insertUser(any(UUID.class), captor.capture());

        User user = captor.getValue();

        assertAnnaFields(user);

        assertThat(insertResult).isEqualTo(1);
    }

    private void assertAnnaFields(User user) {
        assertThat(user.getAge()).isEqualTo(30);
        assertThat(user.getFirstName()).isEqualTo("anna");
        assertThat(user.getLastName()).isEqualTo("montana");
        assertThat(user.getGender()).isEqualTo(User.Gender.FEMALE);
        assertThat(user.getEmail()).isEqualTo("anna@gmail.com");
        assertThat(user.getUserUid()).isNotNull();
        assertThat(user.getUserUid()).isInstanceOf(UUID.class);
    }
}