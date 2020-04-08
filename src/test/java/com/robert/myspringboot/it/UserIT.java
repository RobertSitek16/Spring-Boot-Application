package com.robert.myspringboot.it;

import com.robert.myspringboot.clientproxy.UserControllerV1;
import com.robert.myspringboot.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserIT {

	@Autowired
	private UserControllerV1 userControllerV1;

	@Test
	public void shouldInsertUser() {
		UUID userUid = UUID.randomUUID();
		User user = new User(userUid, "Joe", "Jones",
				User.Gender.MALE, 22, "joe.jones@gmail.com");

		userControllerV1.insertNewUser(user);

		User joe = userControllerV1.fetchUser(userUid);
		assertThat(joe).isEqualToComparingFieldByField(user);
	}

	@Test
	public void shouldDeleteUser() {
		UUID userUid = UUID.randomUUID();
		User user = new User(userUid, "Joe", "Jones",
				User.Gender.MALE, 22, "joe.jones@gmail.com");

		userControllerV1.insertNewUser(user);

		User joe = userControllerV1.fetchUser(userUid);
		assertThat(joe).isEqualToComparingFieldByField(user);

		userControllerV1.deleteUser(userUid);

		assertThatThrownBy(() -> userControllerV1.fetchUser(userUid))
				.isInstanceOf(NotFoundException.class);
	}

	@Test
	public void shouldUpdateUser() {
		UUID userUid = UUID.randomUUID();
		User user = new User(userUid, "Joe", "Jones",
				User.Gender.MALE, 22, "joe.jones@gmail.com");

		userControllerV1.insertNewUser(user);

		User updatedUser = new User(userUid, "Alex", "Jones",
				User.Gender.MALE, 55, "alex.jones@gmail.com");

		userControllerV1.updateUser(updatedUser);

		user = userControllerV1.fetchUser(userUid);
		assertThat(user).isEqualToComparingFieldByField(updatedUser);
	}

	@Test
	public void shouldFetchUsersByGender() {
		UUID userUid = UUID.randomUUID();
		User user = new User(userUid, "Joe", "Jones",
				User.Gender.MALE, 22, "joe.jones@gmail.com");

		userControllerV1.insertNewUser(user);

		List<User> females = userControllerV1.fetchUsers(User.Gender.FEMALE.name());

		assertThat(females).extracting("userUid").doesNotContain(user.getUserUid());
		assertThat(females).extracting("firstName").doesNotContain(user.getFirstName());
		assertThat(females).extracting("lastName").doesNotContain(user.getLastName());
		assertThat(females).extracting("gender").doesNotContain(user.getGender());
		assertThat(females).extracting("age").doesNotContain(user.getAge());
		assertThat(females).extracting("email").doesNotContain(user.getEmail());

		List<User> males = userControllerV1.fetchUsers(User.Gender.MALE.name());

		assertThat(males).extracting("userUid").contains(user.getUserUid());
		assertThat(males).extracting("firstName").contains(user.getFirstName());
		assertThat(males).extracting("lastName").contains(user.getLastName());
		assertThat(males).extracting("gender").contains(user.getGender());
		assertThat(males).extracting("age").contains(user.getAge());
		assertThat(males).extracting("email").contains(user.getEmail());

	}
}
