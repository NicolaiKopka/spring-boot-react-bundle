package com.example.demo;

import com.example.demo.users.MyUser;
import com.example.demo.users.MyUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
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
    public Item getItemById(String id) {
        return kanbanProjectRepo.findById(id).orElseThrow();
    }
    public Item editItem(Item item) {
        return kanbanProjectRepo.save(item);
    }
    public Item moveToNext(Item item){
        StatusEnum newStatus = item.getStatus().next();
        item.setStatus(newStatus);
        return kanbanProjectRepo.save(item);
    }
    public Item moveToPrev(Item item){
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
        return kanbanProjectRepo.save(item);
    }

    public Item deleteItem(String id) {
        Item itemToDelete = kanbanProjectRepo.findById(id).orElseThrow();
        kanbanProjectRepo.delete(itemToDelete);
        return itemToDelete;
    }

}
