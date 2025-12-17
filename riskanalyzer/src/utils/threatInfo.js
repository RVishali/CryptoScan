// src/utils/threatInfo.js

export const getThreatInfo = (type) => {
  const map = {
    'SQL Injection': {
      description:
        'SQL Injection happens when untrusted input is used directly in SQL queries, letting attackers read, change or delete database data.',
      fix:
        'Use parameterized queries / prepared statements, validate and sanitize inputs, and follow least-privilege on database accounts.'
    },

    'Cross-Site Scripting (XSS)': {
      description:
        'XSS allows attackers to inject malicious JavaScript into pages viewed by other users, leading to stolen sessions or UI manipulation.',
      fix:
        'Sanitize and validate user input, encode output before rendering in the browser, and apply a strict Content Security Policy (CSP).'
    },

    'Cross-Site Request Forgery (CSRF)': {
      description:
        'CSRF forces a victim’s browser to send unwanted requests using their logged-in session, performing actions they did not intend.',
      fix:
        'Use anti-CSRF tokens, SameSite cookies, re-authentication for critical actions, and avoid using GET for state-changing operations.'
    },

    'Broken Access Control': {
      description:
        'Broken access control lets users access data or actions beyond their permissions, such as viewing or modifying other users’ data.',
      fix:
        'Enforce server-side authorization checks on every request, use least-privilege, and never rely solely on client-side controls.'
    },

    'Authentication Weaknesses': {
      description:
        'Weak authentication makes it easier for attackers to guess, steal or bypass login credentials and impersonate real users.',
      fix:
        'Use strong password policies, MFA, secure password hashing, and lockout / throttling after repeated failed login attempts.'
    },

    'Security Misconfiguration': {
      description:
        'Security misconfiguration includes default passwords, open admin panels, verbose error messages, and unused services enabled.',
      fix:
        'Harden server and framework settings, disable debug modes in production, limit exposed services, and regularly review configs.'
    },

    'Vulnerable/Outdated Components': {
      description:
        'Using outdated libraries, frameworks or plugins with known vulnerabilities makes exploitation easy for attackers.',
      fix:
        'Maintain a dependency inventory, monitor for CVEs, update regularly, remove unused components, and pin trusted versions.'
    },

    'Sensitive Data Exposure': {
      description:
        'Sensitive data exposure occurs when confidential information is stored or transmitted without proper protection.',
      fix:
        'Use TLS (HTTPS), strong encryption for data at rest, secure key management, and avoid logging or exposing secrets in responses.'
    },

    'Insecure Cookies and Session Management': {
      description:
        'Weak session and cookie handling can allow attackers to hijack active sessions and act as legitimate users.',
      fix:
        'Set HttpOnly, Secure and SameSite flags, rotate session IDs after login, and expire idle/inactive sessions promptly.'
    },

    'Missing Security Headers': {
      description:
        'Missing security headers increases exposure to XSS, clickjacking, MIME-type confusion and other browser-based attacks.',
      fix:
        'Set headers like Content-Security-Policy, X-Frame-Options, X-Content-Type-Options, Referrer-Policy and Strict-Transport-Security.'
    },

    'Insufficient Logging and Monitoring': {
      description:
        'Insufficient logging and monitoring means attacks or suspicious activities are not detected or investigated in time.',
      fix:
        'Log security-relevant events, centralize logs, monitor and alert on anomalies, and retain logs long enough for forensics.'
    },

    'Server-Side Request Forgery (SSRF)': {
      description:
        'SSRF lets attackers trick the server into making HTTP requests to internal or external targets, potentially reaching internal services.',
      fix:
        'Validate and restrict outbound URLs, use allow-lists, block access to internal metadata/management IP ranges, and isolate network segments.'
    }
  };

  return (
    map[type] || {
      description: 'No detailed description is available for this threat yet.',
      fix: 'Review the application and apply general secure coding and hardening practices.'
    }
  );
};
