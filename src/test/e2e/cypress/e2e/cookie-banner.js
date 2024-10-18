beforeEach(() => {
    cy.visit("/");
})

describe('Verify content of cookie-banner on homepage', () => {
    it('should display cookie-banner when visiting for the first time', () => {
        cy.get('body').then(() => {
            cy.contains("Diese Webseite verwendet Cookies")
            cy.contains("Die Cookies sind erforderlich, um die Authorisierung der Webseite zu gewährleisten." +
                " Wir verwenden nur notwendige Cookies, die weder persönliche Daten erfassen noch das Benutzerverhalten tracken." +
                " Die Cookies können durch das verlassen der Webseite abgelehnt werden.")
            cy.contains("Erforderliche zulassen")
        })
    });
});

describe('Verify functionality of cookie-banner on homepage', () => {
    it('should set cookies when clicking accept button', () => {
        cy.getByTestId("cookie-consent-button").click();
        cy.getCookie('cookie-consent').should('have.property', 'value', 'true');
    });

    it('should not re-render cookie-banner after accepting cookies and reloading the site', () => {
        cy.getByTestId("cookie-consent-button").click();
        cy.getCookie('cookie-consent').should('have.property', 'value', 'true');
        cy.reload();

        cy.get('body').then(() => {
            cy.contains("Diese Webseite verwendet Cookies").should('not.exist')
            cy.contains("Die Cookies sind erforderlich, um die Authorisierung der Webseite zu gewährleisten." +
                " Wir verwenden nur notwendige Cookies, die weder persönliche Daten erfassen noch das Benutzerverhalten tracken." +
                " Die Cookies können durch das verlassen der Webseite abgelehnt werden.").should('not.exist')
            cy.contains("Erforderliche zulassen").should('not.exist')
        })
    });
});