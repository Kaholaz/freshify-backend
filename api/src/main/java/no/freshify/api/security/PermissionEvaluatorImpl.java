package no.freshify.api.security;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

public class PermissionEvaluatorImpl implements PermissionEvaluator {

    /**
     * THIS METHOD IS UNSUPPORTED
     * @param authentication     represents the user in question. Should not be null.
     * @param targetDomainObject the domain object for which permissions should be checked. May be null in which case
     *                           implementations should return false, as the null condition can be checked explicitly in
     *                           the expression.
     * @param permission         a representation of the permission object as supplied by the expression system. Not
     *                           null.
     * @return true if the permission is granted, false otherwise
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        throw new UnsupportedOperationException();
    }

    /**
     * Alternative method for evaluating a permission where only the identifier of the target object is available,
     * rather than the target instance itself.
     *
     * @param authentication represents the user in question. Should not be null.
     * @param targetId       the identifier for the object instance (usually a Long)
     * @param targetType     a String representing the target's type (usually a Java classname). Not null.
     * @param permission     a representation of the permission object as supplied by the expression system. Not null.
     * @return true if the permission is granted, false otherwise
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if ((authentication == null) || (targetType == null) || !(permission instanceof String)) {
            return false;
        }
        return hasPrivilege(authentication, (Long) targetId, targetType.toUpperCase(),
                permission.toString().toUpperCase());
    }

    public boolean hasPrivilege(Authentication auth, Long resourceId, String targetType, String permission) {
        return auth.getAuthorities().stream()
                .anyMatch(
                        a -> a.getAuthority().startsWith(targetType) && a.getAuthority().contains(resourceId + "_" + permission)
                );
    }
}
