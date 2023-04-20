package no.freshify.api.repository;

import no.freshify.api.model.Household;
import no.freshify.api.model.HouseholdMemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

interface HouseholdRepository extends JpaRepository<Household, Long> {


    /**
     * Finds the households that a given user belongs to
     * @param userId the user to get households from
     * @return A list of households that the user belongs to
     */
    Optional<List<Household>> findHouseholdsByUser(long userId);

    /**
     * Gets the users that belong to a given household.
     * @param household_id the household to get users from
     * @return A list of users that belong to the household
     */
    Optional<List<Account>> getUsers(long household_id);

    /**
     * Updates the user type of a given user in a given household.
     * @see no.freshify.api.model.HouseholdMemberRole
     * @param household_id The household which the user belongs to
     * @param user_id The user to update
     * @param householdMemberRole The new type to assign to the user
     * @return integer representing the status code
     */
    int updateUserType(long household_id, long user_id, HouseholdMemberRole householdMemberRole);


    /**
     * Adds a user to a household
     * @param household_id The household to add a user to
     * @param user_id The user to add to given household
     * @return integer representing the status code
     */
    int addUser(long household_id, long user_id);

    /**
     * Removes a user from a household
     * @param household_id The houeshold to remove a user from
     * @param user_id The user to remove from given household
     * @return integer representing the status code
     */
    int removeUser(long household_id, long user_id);

    /**
     * Gets a houshold by id
     * @param id The household id to search for
     * @return A household matching the given id
     */
    Optional<Household> getHousehold(long id);

    /**
     * Updates the attributes of a given household.
     * @param name The new name of the household
     * @param household_id The houeshold to update
     * @return integer representing the status code
     */
    int updateHousehold(String name, long household_id);
}
