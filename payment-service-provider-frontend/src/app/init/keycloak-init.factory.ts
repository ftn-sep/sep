import { KeycloakService } from "keycloak-angular";

export function initializeKeycloak(
  keycloak: KeycloakService
  ) {
    return () =>
      keycloak.init({
        config: {
          url: 'http://localhost:8080',
          realm: 'psp-realm',
          clientId: 'frontend',
        },
        initOptions: {
            pkceMethod: 'S256',
            redirectUri: 'http://localhost:4200/',
            checkLoginIframe: false
        }
      });
}