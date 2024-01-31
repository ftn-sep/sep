import { KeycloakService } from "keycloak-angular";

export function initializeKeycloak(
  keycloak: KeycloakService
  ) {
    return () =>
      keycloak.init({
        config: {
          url: 'https://localhost:8443',
          realm: 'psp-realm',
          clientId: 'frontend',
        },
        initOptions: {
            pkceMethod: 'S256',
            redirectUri: 'https://localhost:4200/',
            checkLoginIframe: false
        }
      });
}