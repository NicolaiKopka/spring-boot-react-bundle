package com.example.demo;

import com.example.demo.users.MyUser;
import com.example.demo.users.MyUserRepo;
import org.apache.tomcat.jni.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

class KanbanServiceTest {

//    @Test
//    void shouldSucceedOnAddMethodCall() {
//        MyUser user = new MyUser("1234", "testUser");
//        Item item = new Item("Projekt1", "Beschreibung Projekt 1", StatusEnum.OPEN);
//        Item expectedItem = new Item("1234", "Projekt1", "Beschreibung Projekt 1", StatusEnum.OPEN, "testUser");
//
//        KanbanProjectRepoInterface kanbanProjectRepo = Mockito.mock(KanbanProjectRepoInterface.class);
//
//        MyUserRepo myUserRepo = Mockito.mock(MyUserRepo.class);
//        Mockito.when(myUserRepo.findByUsername("testUser")).thenReturn(Optional.of(user));
//
//        KanbanService kanbanService = new KanbanService(kanbanProjectRepo, myUserRepo);
//        kanbanService.addItem(item, "testUser");
//
//        Mockito.verify(kanbanProjectRepo).save(expectedItem);
//    }

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
        Item item = new Item("Projekt1", "Beschreibung Projekt 1", StatusEnum.OPEN);

        KanbanProjectRepoInterface kanbanProjectRepo = Mockito.mock(KanbanProjectRepoInterface.class);
        Mockito.when(kanbanProjectRepo.save(item)).thenReturn(item);

        MyUserRepo myUserRepo = Mockito.mock(MyUserRepo.class);

        KanbanService kanbanService = new KanbanService(kanbanProjectRepo, myUserRepo);

        kanbanService.moveToNext(item);

        Mockito.verify(kanbanProjectRepo).save(item);
        Assertions.assertThat(item.getStatus()).isEqualTo(StatusEnum.IN_PROGRESS);
    }

    @Test
    void shouldReturnCorrectItemStatusAfterMovedToPrev() {
        Item item = new Item("Projekt1", "Beschreibung Projekt 1", StatusEnum.IN_PROGRESS);

        KanbanProjectRepoInterface kanbanProjectRepo = Mockito.mock(KanbanProjectRepoInterface.class);
        Mockito.when(kanbanProjectRepo.save(item)).thenReturn(item);

        MyUserRepo myUserRepo = Mockito.mock(MyUserRepo.class);

        KanbanService kanbanService = new KanbanService(kanbanProjectRepo, myUserRepo);

        kanbanService.moveToPrev(item);

        Mockito.verify(kanbanProjectRepo).save(item);
        Assertions.assertThat(item.getStatus()).isEqualTo(StatusEnum.OPEN);
    }

    @Test
    void shouldReturnItemById() {
        Item item = new Item("Projekt1", "Beschreibung Projekt 1", StatusEnum.OPEN);

        KanbanProjectRepoInterface kanbanProjectRepo = Mockito.mock(KanbanProjectRepoInterface.class);
        Mockito.when(kanbanProjectRepo.findById("1234")).thenReturn(Optional.of(item));

        MyUserRepo myUserRepo = Mockito.mock(MyUserRepo.class);

        KanbanService kanbanService = new KanbanService(kanbanProjectRepo, myUserRepo);
        Assertions.assertThat(kanbanService.getItemById("1234")).isEqualTo(item);
    }

    @Test
    void shouldEditItem() {
        Item item = new Item("Projekt1", "Beschreibung Projekt 1", StatusEnum.OPEN);

        KanbanProjectRepoInterface kanbanProjectRepo = Mockito.mock(KanbanProjectRepoInterface.class);
        Mockito.when(kanbanProjectRepo.save(item)).thenReturn(item);

        MyUserRepo myUserRepo = Mockito.mock(MyUserRepo.class);

        KanbanService kanbanService = new KanbanService(kanbanProjectRepo, myUserRepo);

        kanbanService.editItem(item);

        Mockito.verify(kanbanProjectRepo).save(item);

    }

}