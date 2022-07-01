package com.example.demo;

import com.example.demo.users.MyUser;
import com.example.demo.users.MyUserRepo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;

import java.util.List;
import java.util.Optional;

class KanbanServiceTest {

    // TODO edge cases not tested yet, Delete not tested
    @Test
    void shouldSucceedOnAddMethodCall() {
        MyUser user = new MyUser("1234", "testUser");
        Item item = new Item("Projekt1", "Beschreibung Projekt 1", StatusEnum.OPEN);
        Item expectedItem = new Item(null, "Projekt1", "Beschreibung Projekt 1", StatusEnum.OPEN, "1234");

        KanbanProjectRepoInterface kanbanProjectRepo = Mockito.mock(KanbanProjectRepoInterface.class);

        MyUserRepo myUserRepo = Mockito.mock(MyUserRepo.class);
        Mockito.when(myUserRepo.findByUsername("testUser")).thenReturn(Optional.of(user));

        KanbanService kanbanService = new KanbanService(kanbanProjectRepo, myUserRepo);
        kanbanService.addItem(item, "testUser");

        Mockito.verify(kanbanProjectRepo).save(expectedItem);
    }

    @Test
    void shouldReturnCollectionOfAllItems() {
        Item item1 = new Item("Projekt1", "Beschreibung Projekt 1", StatusEnum.OPEN);
        Item item2 = new Item("Projekt2", "Beschreibung Projekt 2", StatusEnum.OPEN);

        KanbanProjectRepoInterface kanbanProjectRepo = Mockito.mock(KanbanProjectRepoInterface.class);
        Mockito.when(kanbanProjectRepo.findAll())
                .thenReturn(List.of(item1, item2));

        MyUserRepo myUserRepo = Mockito.mock(MyUserRepo.class);

        KanbanService kanbanService = new KanbanService(kanbanProjectRepo, myUserRepo);
        Assertions.assertThat(kanbanService.getAllItems())
                .isUnmodifiable()
                .contains(item1, item2);
    }

    @Test
    void shouldReturnCorrectItemStatusAfterMovedToNext() {
        MyUser user = new MyUser("1234", "testUser");
        Item item = new Item("itemId", "Projekt1", "Beschreibung Projekt 1", StatusEnum.OPEN, "1234");
        Item expectedItem = new Item("itemId", "Projekt1", "Beschreibung Projekt 1", StatusEnum.IN_PROGRESS, "1234");

        KanbanProjectRepoInterface kanbanProjectRepo = Mockito.mock(KanbanProjectRepoInterface.class);
        Mockito.when(kanbanProjectRepo.save(expectedItem)).thenReturn(expectedItem);
        Mockito.when(kanbanProjectRepo.findById("itemId")).thenReturn(Optional.of(item));

        MyUserRepo myUserRepo = Mockito.mock(MyUserRepo.class);
        Mockito.when(myUserRepo.findByUsername("testUser")).thenReturn(Optional.of(user));

        KanbanService kanbanService = new KanbanService(kanbanProjectRepo, myUserRepo);

        kanbanService.moveToNext(item, "testUser");

        Mockito.verify(kanbanProjectRepo).save(expectedItem);
    }

    @Test
    void shouldReturnCorrectItemStatusAfterMovedToPrev() {
        MyUser user = new MyUser("1234", "testUser");
        Item item = new Item("itemId", "Projekt1", "Beschreibung Projekt 1", StatusEnum.DONE, "1234");
        Item expectedItem = new Item("itemId", "Projekt1", "Beschreibung Projekt 1", StatusEnum.IN_PROGRESS, "1234");

        KanbanProjectRepoInterface kanbanProjectRepo = Mockito.mock(KanbanProjectRepoInterface.class);
        Mockito.when(kanbanProjectRepo.save(expectedItem)).thenReturn(expectedItem);
        Mockito.when(kanbanProjectRepo.findById("itemId")).thenReturn(Optional.of(item));

        MyUserRepo myUserRepo = Mockito.mock(MyUserRepo.class);
        Mockito.when(myUserRepo.findByUsername("testUser")).thenReturn(Optional.of(user));

        KanbanService kanbanService = new KanbanService(kanbanProjectRepo, myUserRepo);

        kanbanService.moveToPrev(item, "testUser");

        Mockito.verify(kanbanProjectRepo).save(expectedItem);
    }

    @Test
    void shouldReturnItemById() {
        MyUser user = new MyUser("1234", "testUser");
        Item item = new Item("itemId", "Projekt1", "Beschreibung Projekt 1", StatusEnum.DONE, "1234");

        KanbanProjectRepoInterface kanbanProjectRepo = Mockito.mock(KanbanProjectRepoInterface.class);
        Mockito.when(kanbanProjectRepo.findById("itemId")).thenReturn(Optional.of(item));

        MyUserRepo myUserRepo = Mockito.mock(MyUserRepo.class);
        Mockito.when(myUserRepo.findByUsername("testUser")).thenReturn(Optional.of(user));

        KanbanService kanbanService = new KanbanService(kanbanProjectRepo, myUserRepo);
        Assertions.assertThat(kanbanService.getItemById("itemId", "testUser")).isEqualTo(item);
    }

    @Test
    void shouldEditItem() {
        MyUser user = new MyUser("1234", "testUser");
        Item item = new Item("itemId", "Projekt1", "Beschreibung Projekt 1", StatusEnum.DONE, "1234");

        KanbanProjectRepoInterface kanbanProjectRepo = Mockito.mock(KanbanProjectRepoInterface.class);
        Mockito.when(kanbanProjectRepo.save(item)).thenReturn(item);
        Mockito.when(kanbanProjectRepo.findById("itemId")).thenReturn(Optional.of(item));

        MyUserRepo myUserRepo = Mockito.mock(MyUserRepo.class);
        Mockito.when(myUserRepo.findByUsername("testUser")).thenReturn(Optional.of(user));

        KanbanService kanbanService = new KanbanService(kanbanProjectRepo, myUserRepo);

        kanbanService.moveToPrev(item, "testUser");

        Mockito.verify(kanbanProjectRepo).save(item);

    }

}