package com.user.service;

import com.user.exception.UserNotFoundException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    private UserService userService;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
    }

    @Test
    public void testCreateUser() {
        User user = new User("John", "john@test.com");

        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser("John", "john@test.com");

        Assert.assertEquals(result.getName(), "John");
        Assert.assertEquals(result.getEmail(), "john@test.com");

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testGetUserById() {
        User user = new User("John", "john@test.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User result = userService.getUserById(1L);

        Assert.assertEquals(result.getName(), "John");
        verify(userRepository, times(1)).findById(any(Long.class));
    }

    @Test(expectedExceptions = UserNotFoundException.class)
    public void testGetUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        userService.getUserById(1L);
        verify(userRepository, times(1)).findById(any(Long.class));
    }

    @Test
    public void testUpdateUser() {
        User existingUser = new User("Old", "old@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User updated = userService.updateUser(1L, "New", "new@test.com");

        Assert.assertEquals(updated.getName(), "New");
        Assert.assertEquals(updated.getEmail(), "new@test.com");

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testDeleteUser() {
        User user = new User("John", "john@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(user);

    }
}
