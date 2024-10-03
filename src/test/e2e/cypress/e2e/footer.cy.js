beforeEach(() => {
    cy.visit("/");
    cy.getByTestId("cookie-consent-button").click();
})

describe('Verify content of footer', () => {
    it('should display the right image', () => {
        cy.getByTestId("swissuniversities-logo")
            .should('have.attr', 'src')
            .and('include', 'swissuniversities.png');
    })

    it('should have link to swissuniversities', () => {
        cy.getByTestId("swissuniversities-link")
            .should('have.attr', 'href')
            .and('include', 'https://www.swissuniversities.ch/');
    });

    it('should have link to impressum', () => {
        cy.getByTestId("impressum-link")
            .should('have.attr', 'href')
            .and('include', 'https://www.unilu.ch/impressum/');
    });

    it('should display version', () => {
        cy.getByTestId("version")
            .contains('Version')
    });
});
