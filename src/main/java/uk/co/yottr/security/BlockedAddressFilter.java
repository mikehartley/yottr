package uk.co.yottr.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.co.yottr.service.BlockedAddressService;

import java.io.IOException;

/**
 * Turns away requests from blocked addresses before anything else sees them, with a plain 403.
 * BlockedAddressConfig puts it first in the chain.
 */
public class BlockedAddressFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(BlockedAddressFilter.class);

    private final BlockedAddressService blockedAddressService;

    public BlockedAddressFilter(BlockedAddressService blockedAddressService) {
        this.blockedAddressService = blockedAddressService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (blockedAddressService.isBlocked(request.getRemoteAddr())) {
            LOG.debug("turned away {} from {}", request.getRequestURI(), request.getRemoteAddr());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("Requests from your address have been blocked.");
            return;
        }
        chain.doFilter(request, response);
    }
}
