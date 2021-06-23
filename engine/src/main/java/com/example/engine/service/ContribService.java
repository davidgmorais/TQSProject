package com.example.engine.service;

import com.example.engine.dto.ContribDTO;
import com.example.engine.entity.Contrib;
import com.example.engine.entity.User;

import java.util.Map;
import java.util.List;

public interface ContribService {
    Contrib create(ContribDTO user);
    Contrib verifyContributor(int contribId);
    boolean denyContributor(int contribId);
    List<Contrib> getAllContributors();
    List<Contrib> getAllContributorsRequests();
    Boolean isVerified(User user);
    List<Contrib> search(Map<String, String> filters);
    Contrib getContributorByUsername(String username);
    Contrib getContributorById(int contribId);
}
