package com.example.engine.service;

import com.example.engine.dto.ContribDTO;
import com.example.engine.entity.Contrib;
import com.example.engine.entity.User;
import com.example.engine.repository.ContribRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ContribServiceImpl implements ContribService{

    @Autowired
    ContribRepository repository;

    @Autowired
    UserService userService;

    @Override
    public Contrib create(ContribDTO user) {

        // map ContribDTO to persistent Contrib User
        var persistentUser = new User();
        persistentUser.setEmail(user.getEmail());
        persistentUser.setUsername(user.getUsername());
        persistentUser.setFirstName(user.getFirstName());
        persistentUser.setLastName(user.getLastName());
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        persistentUser.setPassword(encoder.encode(user.getPassword()));
        persistentUser.setRole(2);

        var saved = userService.register(persistentUser);
        if (saved != null) {
            var contribData = new Contrib(saved, user.getStoreName());
            return repository.save(contribData);
        }

        return null;
    }

    @Override
    public Contrib verifyContributor(int contribId) {

        var contribToVerify = repository.findContribById(contribId);

        if (contribToVerify != null) {
            contribToVerify.setVerified(true);
            return repository.save(contribToVerify);
        }
        return null;
    }

    @Override
    public boolean denyContributor(int contribId) {
        var contribToDeny = repository.findContribById(contribId);
        if (contribToDeny != null) {
            repository.delete(contribToDeny);
            return true;
        }
        return false;
    }

    @Override
    public Boolean isVerified(User user) {
        var contrib = repository.getContribByUserId(user.getId());
        return contrib.getVerified();
    }

    @Override
    public List<Contrib> search(Map<String, String> filters) {
        var userKey = "username";
        var serviceKey = "serviceName";
        ArrayList<Contrib> results = new ArrayList<>();

        if (filters.containsKey(userKey) && filters.containsKey(serviceKey)) {
            results.addAll(repository.findContribByVerifiedTrueAndUserUsernameContainingIgnoreCase(filters.get(userKey)));
            results.retainAll(repository.findContribByVerifiedTrueAndStoreNameContainingIgnoreCase(filters.get(serviceKey)));
        } else if (filters.containsKey(userKey)) {
            results.addAll(repository.findContribByVerifiedTrueAndUserUsernameContainingIgnoreCase(filters.get(userKey)));
        } else {
            results.addAll(repository.findContribByVerifiedTrueAndStoreNameContainingIgnoreCase(filters.get(serviceKey)));
        }

        return results;
    }

    @Override
    public List<Contrib> getAllContributors() {
        return repository.findAllByVerifiedTrue();
    }

    @Override
    public List<Contrib> getAllContributorsRequests() {
        return repository.findAllByVerifiedFalse();
    }

    @Override
    public Contrib getContributorByUsername(String username) {
        return repository.getContribByUserUsername(username);
    }
}
