package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

class ItemRepositoryImplTest {

    @Test
    void itemsSearch() {
        ItemRepository itemRepository = new ItemRepositoryImpl();
        UserRepository userRepository = new UserRepositoryImpl(itemRepository);
        ItemMapper itemMapper = new ItemMapper();
        ItemService itemService = new ItemServiceImpl(itemRepository, userRepository, itemMapper);
        User user1 = new User();
        user1.setEmail("user1@email.com");
        user1.setName("user1");
        User user2 = new User();
        user2.setEmail("user2@email.com");
        user2.setName("user2");
        userRepository.create(user1);
        userRepository.create(user2);
        Item item1 = new Item();
        item1.setName("Тест один");
        item1.setOwnerId(1L);
        item1.setAvailable(true);
        Item item2 = new Item();
        item2.setDescription("Тест два");
        item2.setOwnerId(2L);
        item2.setAvailable(true);

        itemRepository.save(item1);
        itemRepository.save(item2);

        System.out.println(itemRepository.itemsSearch("тест"));
        System.out.println(itemService.searchItems("тест"));
    }
}