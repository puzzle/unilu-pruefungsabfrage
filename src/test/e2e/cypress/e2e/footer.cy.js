beforeEach(() => {
    cy.visit("/");
})

describe('Verify content of footer', () => {
    it('should display the right image', () => {
        cy.getByTestId("swissuniversities-logo")
            .should('have.attr', 'src')
            .and('include', 'swissuniversities.png');
    })

    it('should redirect to swissuniversities', () => {
        cy.getByTestId("swissuniversities-link")
            .should('have.attr', 'href')
            .and('include', 'https://www.swissuniversities.ch/');
    });

    it('should redirect to impressum', () => {
        cy.getByTestId("impressum-link")
            .should('have.attr', 'href')
            .and('include', 'https://www.unilu.ch/impressum/');
    });

    it('should redirect to github-page', () => {
        cy.getByTestId("version-link")
            .should('have.attr', 'href')
            .and('include', 'https://github.com/puzzle/unilu-pruefungsabfrage');
    });
});
