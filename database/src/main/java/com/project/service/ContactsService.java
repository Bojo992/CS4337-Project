package com.project.service;

import com.project.model.Contacts;
import com.project.repository.ContactsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContactsService {
    @Autowired
    private ContactsRepository contactRepository;

    public Optional<Contacts> getContactById(Long id) {
        return contactRepository.findById(id);
    }

    public void createContact(Contacts contact) {
        contactRepository.save(contact);
    }

    public void updateContact(Long id, Contacts contact) {
        contactRepository.findById(id).ifPresent(_ -> contactRepository.save(contact));
    }

    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }
}
