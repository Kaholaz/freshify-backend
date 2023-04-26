package no.freshify.api.service;

import no.freshify.api.exception.HouseholdNotFoundException;
import no.freshify.api.exception.UserNotFoundException;
import no.freshify.api.model.*;
import no.freshify.api.model.dto.UserFull;
import no.freshify.api.model.mapper.UserMapper;
import no.freshify.api.repository.HouseholdMemberRepository;
import no.freshify.api.repository.HouseholdRepository;
import no.freshify.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class HouseholdServiceTest {

    @Mock
    private HouseholdRepository householdRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private HouseholdMemberRepository householdMemberRepository;
    @InjectMocks
    private HouseholdService householdService;

    private final Long householdId = 1L;

    private Household household;
    private User loggedInUser;
    private User user1;
    private List<HouseholdMember> user1HouseholdMembers;
    private User user2;
    private List<UserFull> householdMembersUserFull;
    private List<HouseholdMember> householdMembers;
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @BeforeEach
    public void setup() {
        household = new Household();
        household.setId(householdId);
        household.setName("Household");

        loggedInUser = new User();
        loggedInUser.setEmail("test@example.com");
        loggedInUser.setPassword("password");
        loggedInUser.setFirstName("Test");
        loggedInUser.setId(10L);

        user1 = new User();
        user1.setId(1L);
        user1.setEmail("johndoe@example.com");
        user1.setPassword("password");
        user1.setFirstName("John");

        user2 = new User();
        user2.setId(2L);
        user2.setEmail("janedoe@example.com");
        user2.setPassword("password");
        user2.setFirstName("Jane");

        var memberKey1 = new HouseholdMemberKey();
        memberKey1.setHouseholdId(1L);
        memberKey1.setUserId(1L);

        var memberKey2 = new HouseholdMemberKey();
        memberKey2.setHouseholdId(1L);
        memberKey2.setUserId(2L);

        HouseholdMember householdMember1 = new HouseholdMember(memberKey1, household, user1, HouseholdMemberRole.SUPERUSER);
        HouseholdMember householdMember2 = new HouseholdMember(memberKey2, household, user2, HouseholdMemberRole.USER);

        householdMember1.setHousehold(household);
        householdMember2.setHousehold(household);

        this.user1HouseholdMembers = new ArrayList<>(List.of(householdMember1));

        this.householdMembers = new ArrayList<>(List.of(householdMember1, householdMember2));
        Set<HouseholdMember> householdMembers = new HashSet<>(List.of(householdMember1, householdMember2));
        this.householdMembersUserFull = new ArrayList<>();
        for (HouseholdMember member : householdMembers) {
            this.householdMembersUserFull.add(userMapper.toUserFull(member.getUser()));
        }
        household.setHouseholdMembers(householdMembers);
        householdService.addHousehold(household);
        householdRepository.save(household);
    }

    @Test
    public void testFindHouseholdByHouseholdId() throws HouseholdNotFoundException {
        Mockito.when(householdRepository.findHouseholdById(householdId)).thenReturn(household);

        Household result = householdService.findHouseholdByHouseholdId(householdId);

        assertEquals(household, result);
    }

    @Test
    public void testFindHouseholdByHouseholdIdThrowsHouseholdNotFoundException() {
        assertThrows(HouseholdNotFoundException.class, () -> {
            householdService.findHouseholdByHouseholdId(12345L);
        });
    }

    @Test
    public void testGetUsers() throws HouseholdNotFoundException {
        List<HouseholdMember> members = new ArrayList<>(household.getHouseholdMembers());
        Mockito.when(householdRepository.existsById(householdId)).thenReturn(true);
        Mockito.when(householdMemberRepository.findHouseholdMembersByHouseholdId(householdId))
                .thenReturn(members);

        List<UserFull> result = householdService.getUsers(householdId);

        assertEquals(this.householdMembersUserFull.size(), result.size());
    }

    @Test
    public void testGetUsersThrowsHouseholdNotFoundException() {
        // Call the getUsers method with an invalid household id
        assertThrows(HouseholdNotFoundException.class, () -> {
            householdService.getUsers(12345L);
        });
    }

    @Test
    public void testAddHousehold() {
        Mockito.when(householdRepository.save(household)).thenReturn(household);

        Household result = householdService.addHousehold(household);

        assertEquals(this.household, result);
    }

    @Test
    public void testRemoveHousehold() throws HouseholdNotFoundException {
        willDoNothing().given(householdRepository).deleteById(householdId);
        Mockito.when(householdRepository.existsById(householdId)).thenReturn(true);

        householdService.removeHousehold(householdId);

        verify(householdRepository, times(1)).deleteById(householdId);
    }

    @Test
    public void testRemoveHouseholdThrowsHouseholdNotFoundException() {
        assertThrows(HouseholdNotFoundException.class, () -> {
            householdService.removeHousehold(12345L);
        });
    }

    @Test
    public void testUpdateHousehold() {
        Mockito.when(householdRepository.saveAndFlush(household))
                        .thenReturn(household);
        household.setName("New name");

        Household updatedHousehold = householdService.updateHousehold(household);

        assertEquals(updatedHousehold.getName(), "New name");
    }

    @Test
    public void testGetHouseholds() throws UserNotFoundException {
        Mockito.when(userRepository.existsById(user1.getId()))
                        .thenReturn(true);
        Mockito.when(householdMemberRepository.findHouseholdMembersByUserId(user1.getId()))
                .thenReturn(this.user1HouseholdMembers);

        List<Household> result = householdService.getHouseholds(user1.getId());

        assertEquals(List.of(household), result);
    }

    @Test
    public void testGetHouseholdsThrowsUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> {
            householdService.getHouseholds(12345L);
        });
    }
}
