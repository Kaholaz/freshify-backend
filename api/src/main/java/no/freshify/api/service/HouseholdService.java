package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.model.Household;
import no.freshify.api.repository.HouseholdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HouseholdService {
    @Autowired
    private final HouseholdRepository householdRepository;

    public Household findHouseholdByHouseholdId(Long householdId) {
        return householdRepository.findHouseholdById(householdId);
    }

}
