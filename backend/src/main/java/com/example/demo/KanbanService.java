package com.example.demo;

import com.example.demo.users.MyUser;
import com.example.demo.users.MyUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class KanbanService {
    private final KanbanProjectRepoInterface kanbanProjectRepo;
    private final MyUserRepo myUserRepo;

    public Collection<Item> getAllItems() {
        return kanbanProjectRepo.findAll();
    }

    public Collection<Item> getItemsByUser(String username) {
        MyUser user = myUserRepo.findByUsername(username).orElseThrow();
        return kanbanProjectRepo.findAllByUserId(user.getId());
    }
    public Item getItemById(String id, String username) {
        MyUser currentUser = getCurrentUser(username);
        Item searchItem = kanbanProjectRepo.findById(id).orElseThrow();
        if(!searchItem.getUserId().equals(currentUser.getId())) {
            throw new IllegalStateException("You are not the user of this item");
        }
        return searchItem;
    }
    public Item editItem(Item item, String username) {
        MyUser currentUser = getCurrentUser(username);
        Item searchItem = kanbanProjectRepo.findById(item.getId()).orElseThrow();
        if(!searchItem.getUserId().equals(currentUser.getId())) {
            throw new IllegalStateException("You are not the user of this item");
        }
        return kanbanProjectRepo.save(item);
    }
    public Item moveToNext(Item item, String username){
        Item currentItem = getItemById(item.getId(), username);
        MyUser currentUser = getCurrentUser(username);
        if(!currentItem.getUserId().equals(currentUser.getId())) {
            throw new IllegalStateException("You are not the user of this item");
        }
        StatusEnum newStatus = item.getStatus().next();
        item.setStatus(newStatus);
        return kanbanProjectRepo.save(item);
    }
    public Item moveToPrev(Item item, String username){
        Item currentItem = getItemById(item.getId(), username);
        MyUser currentUser = getCurrentUser(username);
        if(!currentItem.getUserId().equals(currentUser.getId())) {
            throw new IllegalStateException("You are not the user of this item");
        }
        StatusEnum newStatus = item.getStatus().prev();
        item.setStatus(newStatus);
        return kanbanProjectRepo.save(item);
    }
    public Item addItem(Item item, String username) {
        if("".equals(item.getTask()) || "".equals(item.getDescription())) {
            throw new RuntimeException("Empty inputs detected");
        }

//        Optional<Item> itemFromDB = kanbanProjectRepo.findById(item.getId());
//        if(itemFromDB.isPresent()) {
//            if(itemFromDB.get().getTask().equals(item.getTask()) && itemFromDB.get().getDescription().equals(item.getDescription())){
//                throw new RuntimeException("Identical inputs detected");
//            }
//        }
        MyUser user = myUserRepo.findByUsername(username).orElseThrow();
        item.setUserId(user.getId());
        item.setStatus(StatusEnum.OPEN);
        return kanbanProjectRepo.save(item);
    }
    public Item deleteItem(String id, String username) {
        MyUser currentUser = getCurrentUser(username);
        Item itemToDelete = kanbanProjectRepo.findById(id).orElseThrow();
        if(!itemToDelete.getUserId().equals(currentUser.getId())) {
            throw new IllegalStateException("You are not the user of this item");
        }
        kanbanProjectRepo.delete(itemToDelete);
        return itemToDelete;
    }
    private MyUser getCurrentUser(String username) {
        return myUserRepo.findByUsername(username).orElseThrow();
    }


}
