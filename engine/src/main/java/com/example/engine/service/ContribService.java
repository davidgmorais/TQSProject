package com.example.engine.service;

import com.example.engine.dto.ContribDTO;
import com.example.engine.entity.Contrib;

import java.util.List;

public interface ContribService {
    Contrib create(ContribDTO user);
    Contrib verifyContributor(int contribId);
    List<Contrib> getAllContributors();
    List<Contrib> getAllContributorsRequests();
}
