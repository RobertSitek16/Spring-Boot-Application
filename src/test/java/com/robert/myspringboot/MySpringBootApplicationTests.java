package com.robert.myspringboot;

import com.robert.myspringboot.clientproxy.UserControllerV1;
import com.robert.myspringboot.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class MySpringBootApplicationTests {

	@Autowired
	private UserControllerV1 userControllerV1;

	@Test
	public void itShouldFetchAllUsers() {
		List<User> users = userControllerV1.fetchUsers(null);

		assertThat(users).hasSize(1);

		User joe = new User(null, "Joe", "Jones",
				User.Gender.MALE, 22, "joe.jones@gmail.com");

		assertThat(users.get(0)).isEqualToIgnoringGivenFields(joe, "userUid");
		assertThat(users.get(0).getUserUid()).isInstanceOf(UUID.class);
		assertThat(users.get(0).getUserUid()).isNotNull();
	}

}
