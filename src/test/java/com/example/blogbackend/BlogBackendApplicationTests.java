package com.example.blogbackend;

import com.example.blogbackend.entity.BoardEntity;
import com.example.blogbackend.entity.UserEntity;
import com.example.blogbackend.entity.model.Category;
import com.example.blogbackend.repository.BoardRepository;
import com.example.blogbackend.repository.CategoryRepository;
import com.example.blogbackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BlogBackendApplicationTests {

	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Test
	void contextLoads() {
	}
	@Test
	@Transactional
	@Rollback(value = false)
	void setUserRepositoryTest() {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encPassword = passwordEncoder.encode("test_password");
		UserEntity userEntity = UserEntity.builder()
				.userId("test_user")
				.userPw(encPassword)
				.userName("테스트유저")
				.build();

		UserEntity savedUser = userRepository.save(userEntity);
		assertThat(userEntity.getUserId()).isEqualTo(savedUser.getUserId());
	}

	@Test
	@Transactional
	@Rollback(value = false)
	void setBoardRepositoryTest() {
		List<Category> list = categoryRepository.findAll();
		Random random = new Random();

		for (int i = 1; i <= 200; i++) {
			// Select a random category from the list
			Category randomCategory = list.get(random.nextInt(list.size()));

			// Save the board entity with random category and current timestamp
			boardRepository.save(BoardEntity.builder()
					.title("게시글 제목" + i)
					.contents("게시글 내용" + i)
					.author("작성자" + i)
					.category(randomCategory)
					.createdAt(LocalDateTime.now()) // Use current date and time
					.build());
		}
	}
}
