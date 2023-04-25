package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.HouseholdMemberAlreadyExistsException;
import no.freshify.api.exception.UserDoesNotBelongToHouseholdException;
import no.freshify.api.model.HouseholdMember;
import no.freshify.api.model.HouseholdMemberKey;
import no.freshify.api.repository.HouseholdMemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseholdMemberService {
    private final HouseholdMemberRepository householdMemberRepository;

    Logger logger = LoggerFactory.getLogger(HouseholdMemberService.class);

    public HouseholdMember addHouseholdMember(HouseholdMember householdMember) throws HouseholdMemberAlreadyExistsException {
        if (householdMemberExists(householdMember.getId())) {
            logger.warn("Household member already exists");
            throw new HouseholdMemberAlreadyExistsException();
        }
        return householdMemberRepository.save(householdMember);
    }

    public boolean householdMemberExists(HouseholdMemberKey householdMemberKey) {
        return householdMemberRepository.existsById(householdMemberKey);
    }

    public HouseholdMember getHouseholdMemberByHouseholdMemberKey(HouseholdMemberKey householdMemberKey) throws UserDoesNotBelongToHouseholdException {
        if (this.householdMemberExists(householdMemberKey)) {
            return householdMemberRepository.findById(householdMemberKey);
        }

        logger.warn("User does not belong to household");
        throw new UserDoesNotBelongToHouseholdException();
    }

    public HouseholdMember updateHouseholdMember(HouseholdMember householdMember) {
        householdMemberRepository.save(householdMember);
        return householdMember;
    }

    public List<HouseholdMember> getHouseHoldMembersByUserId(Long userId) {
        return householdMemberRepository.findHouseholdMembersByUserId(userId);
    }

    public void removeHouseholdMember(HouseholdMember householdMember) {
        householdMemberRepository.delete(householdMember);
    }
}
