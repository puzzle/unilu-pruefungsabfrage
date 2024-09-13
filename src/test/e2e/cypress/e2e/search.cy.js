beforeEach(() => {
    cy.visit("/search");
    cy.task('clearDownloads');
})

it('should show info message on search page', () => {
    cy.get('body').then((body) => {
        expect(body).to.contain("Suche nach einer Prüfung...")
    })
})

it('should display header on search page', () => {
    cy.get('#logo').should("be.visible");
})

it('should display label for searchbar', () => {
    cy.get('body').then((body) => {
        expect(body).to.contain("Prüfungsnummer eingeben*:")
    })
});

it('should display input for exam number', () => {
    cy.get('input[type="text"]').should('exist');
});

it('should display results when searching with valid exam number', () => {
    cy.get('input[type="text"]').type('11000');
    cy.get('button').click();
    cy.contains('Privatrecht');
});

it('should display error when no exams were found', () => {
    cy.get('input[type="text"]').type('99999');
    cy.get('button').click();
    cy.contains('Keine Prüfungen für die Prüfungslaufnummer 99999 gefunden');
});

it('should display error when exam number is invalid', () => {
    cy.get('input[type="text"]').type('abc');
    cy.get('button').click();
    cy.contains('Prüfungsnummer muss 5 Ziffern lang sein');
});

it('should display download as ZIP button when input is valid', () => {
    cy.get('input[type="text"]').type('11000');
    cy.get('button').click();
    cy.contains('Alle als ZIP herunterladen');
});

it('should not display ZIP download when exam number is invalid', () => {
    cy.get('input[type="text"]').type('abc');
    cy.get('button').click();
    cy.get('Alle als ZIP herunterladen').should('not.exist');
});

it('should download file when link is clicked', () => {
    cy.get('input[type="text"]').type('11000');
    cy.get('button').click();
    cy.contains('Privatrecht').click();
    cy.readFile('cypress/downloads/Privatrecht.pdf').should('exist');
});

it('should show downloadable files with name of subject-folder they are in', () => {
    cy.get('input[type="text"]').type('11000');
    cy.get('button').click();
    cy.contains('Handels und Gesellschaftsrecht');
    cy.contains('Privatrecht');
    cy.contains('Strafrecht');
    cy.contains('Öffentliches Recht')
});

it('should rename files to subject-folder they are inside of after downloading', () => {
    cy.get('input[type="text"]').type('11000');
    cy.get('button').click();
    const subjects = ['Handels und Gesellschaftsrecht', 'Privatrecht', 'Strafrecht', 'Öffentliches Recht']

    for (let i = 0; i < subjects.length; i++) {
        cy.contains(subjects[i]).click();
        cy.readFile(`cypress/downloads/${subjects[i]}.pdf`).should('exist');
    }
});
