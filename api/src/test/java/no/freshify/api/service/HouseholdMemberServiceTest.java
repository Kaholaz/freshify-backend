package no.freshify.api.service;

import no.freshify.api.exception.HouseholdMemberAlreadyExistsException;
import no.freshify.api.exception.UserDoesNotBelongToHouseholdException;
import no.freshify.api.model.Household;
import no.freshify.api.model.HouseholdMember;
import no.freshify.api.model.HouseholdMemberKey;
import no.freshify.api.model.User;
import no.freshify.api.repository.HouseholdMemberRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HouseholdMemberServiceTest {

    @Mock
    private HouseholdMemberRepository householdMemberRepository;

    @InjectMocks
    private HouseholdMemberService householdMemberService;


    private User user;
    private Household household;
    private HouseholdMember householdMember;
    private HouseholdMemberKey householdMemberKey;

    private final Long userId = 1L;
    private final Long householdId = 1L;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        household = new Household();
        householdMember = new HouseholdMember();
        householdMemberKey = new HouseholdMemberKey(householdId, userId);

        householdMember.setId(householdMemberKey);

        household.setId(householdId);
        user.setId(userId);

        householdMember.setUser(user);
        householdMember.setHousehold(household);
    }

    @Test
    public void testAddHouseholdMember() throws HouseholdMemberAlreadyExistsException {
        Mockito.when(householdMemberService.householdMemberExists(householdMemberKey)).thenReturn(false);

        householdMemberService.addHouseholdMember(householdMember);

        Mockito.verify(householdMemberRepository, Mockito.times(1)).save(householdMember);
    }


    @Test
    public void testAddHouseholdMemberThrowsException() {
        Mockito.when(householdMemberService.householdMemberExists(householdMemberKey)).thenReturn(true);

        assertThrows(HouseholdMemberAlreadyExistsException.class, () -> {
            householdMemberService.addHouseholdMember(householdMember);
        });
    }

    @Test
    public void testHouseholdMemberExists() {
        Mockito.when(householdMemberRepository.existsById(householdMemberKey)).thenReturn(true);

        boolean result = householdMemberService.householdMemberExists(householdMemberKey);

        assertTrue(result);
    }

    @Test
    public void testGetHouseholdMemberByHouseholdMemberKey() throws UserDoesNotBelongToHouseholdException {
        Mockito.when(householdMemberRepository.existsById(householdMemberKey)).thenReturn(true);
        Mockito.when(householdMemberRepository.findById(householdMemberKey)).thenReturn(householdMember);

        HouseholdMember result = householdMemberService.getHouseholdMemberByHouseholdMemberKey(householdMemberKey);

        assertEquals(householdMember, result);
    }

    @Test
    public void testGetHouseholdMemberByHouseholdMemberKeyThrowsException() throws UserDoesNotBelongToHouseholdException {
        Mockito.when(householdMemberRepository.existsById(householdMemberKey)).thenReturn(false);

        assertThrows(UserDoesNotBelongToHouseholdException.class, () ->
            householdMemberService.getHouseholdMemberByHouseholdMemberKey(householdMemberKey));
    }

    @Test
    public void testUpdateHouseholdMember() {
        householdMemberService.updateHouseholdMember(householdMember);

        Mockito.verify(householdMemberRepository, Mockito.times(1)).save(householdMember);
    }

    @Test
    public void testGetHouseHoldMembersByUserId() {
        Mockito.when(householdMemberRepository.findHouseholdMembersByUserId(userId)).thenReturn(Collections.singletonList(householdMember));

        List<HouseholdMember> result = householdMemberService.getHouseHoldMembersByUserId(userId);

        assertEquals(Collections.singletonList(householdMember), result);
    }

    @Test
    public void testRemoveHouseholdMember() {
        householdMemberService.removeHouseholdMember(householdMember);

        Mockito.verify(householdMemberRepository, Mockito.times(1)).delete(householdMember);
    }

}