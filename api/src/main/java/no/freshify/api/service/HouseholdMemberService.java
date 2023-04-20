package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.HouseholdMemberAlreadyExistsException;
import no.freshify.api.model.HouseholdMember;
import no.freshify.api.model.HouseholdMemberKey;
import no.freshify.api.repository.HouseholdMemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HouseholdMemberService {
    private final HouseholdMemberRepository householdMemberRepository;

    Logger logger = LoggerFactory.getLogger(HouseholdMemberService.class);

    public void addHouseholdMember(HouseholdMember householdMember) throws HouseholdMemberAlreadyExistsException {
        if (householdMemberExists(householdMember.getId())) {
            logger.warn("Household member already exists");
            throw new HouseholdMemberAlreadyExistsException();
        }
        householdMemberRepository.saveAndFlush(householdMember);
    }

    public boolean householdMemberExists(HouseholdMemberKey householdMemberKey) {
        return householdMemberRepository.existsById(householdMemberKey);
    }
}
