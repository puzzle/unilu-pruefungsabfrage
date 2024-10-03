beforeEach(() => {
    cy.visit("/search");
})

describe('Verify content of search page', () => {
    it('should show info message on search page', () => {
        cy.get('body').then((body) => {
            expect(body).to.contain("Suche nach einer Prüfung...")
        })
    })

    it('should display header image on search page', () => {
        cy.get('#logo').should("be.visible");
    })

    it('should display label for searchbar', () => {
        cy.get('body').then(() => {
            cy.contains("Prüfungsnummer eingeben:")
        })
    });

    it('should display input for exam number', () => {
        cy.getByTestId("exam-number-input").should('exist');
    });
})

describe('Verify searchbar functionality', () => {
    it('should display results when searching with valid exam number', () => {
        cy.getByTestId("exam-number-input").type('11000');
        cy.getByTestId("submit-button").click();
        cy.contains('Privatrecht');
    });

    it('should display error when no exams were found', () => {
        cy.getByTestId("exam-number-input").type('99999');
        cy.getByTestId("submit-button").click();
        cy.contains('Keine Prüfungen für die Prüfungslaufnummer 99999 gefunden');
    });

    it('should display error when exam number is too short', () => {
        cy.getByTestId("exam-number-input").type('123');
        cy.getByTestId("submit-button").click();
        cy.contains('Prüfungsnummer muss aus genau 5 Ziffern bestehen.');
    });

    it('should display error when input consists of letters', () => {
        cy.get('input[type="text"]').type('asdf');
        cy.getByTestId("submit-button").click();
    cy.contains('Prüfungsnummer muss aus genau 5 Ziffern bestehen.');
    });

    it('should display error when input consists of a mix between letters and numbers', () => {
        cy.get('input[type="text"]').type('wow25');
        cy.getByTestId("submit-button").click();
        cy.contains('Prüfungsnummer muss aus genau 5 Ziffern bestehen.');
    });

    it('should not be able to input more than 5 characters', () => {
        cy.getByTestId("exam-number-input").type('110000')
            .should('have.value', '11000');
    });

    it('should display download as ZIP button when input is valid', () => {
        cy.getByTestId("exam-number-input").type('11000');
        cy.getByTestId("submit-button").click();
        cy.contains('Alle als ZIP herunterladen');
    });

    it('should not display ZIP download when exam number is invalid', () => {
        cy.getByTestId("exam-number-input").type('123');
        cy.getByTestId("submit-button").click();
        cy.get('Alle als ZIP herunterladen').should('not.exist');
    });
})

describe('Verify download functionality', () => {
    it('should show downloadable files with name of subject-folder they are in', () => {
        cy.getByTestId("exam-number-input").type('11000');
        cy.getByTestId("submit-button").click();
        cy.contains('Handels und Gesellschaftsrecht');
        cy.contains('Privatrecht');
        cy.contains('Strafrecht');
        cy.contains('Öffentliches Recht')
    });

    it('should show files in alphabetical order', () => {
        cy.getByTestId("exam-number-input").type('11000');
        cy.getByTestId("submit-button").click();
        cy.scrollTo('bottom');
    cy.get('[data-testid="exam-files"]').then(files => {
            const fileNames = [...files].map(file => file.innerText);
            const sortedFileNames = ["Handels und Gesellschaftsrecht", "Öffentliches Recht", "Privatrecht", "Strafrecht"];
            expect(fileNames).to.deep.equal(sortedFileNames)
        })
    });

    it('should download files with correct name when link is clicked', () => {
        cy.getByTestId("exam-number-input").type('11000');
        cy.getByTestId("submit-button").click();
        const subjects = ['Handels und Gesellschaftsrecht', 'Privatrecht', 'Strafrecht']

        for (let i = 0; i < subjects.length; i++) {
            cy.contains(subjects[i]).click();
            cy.readFile(`cypress/downloads/${subjects[i]}.pdf`).should('exist');
        }
    });

    it('should download ZIP file', () => {
        cy.getByTestId("exam-number-input").type('11000');
        cy.getByTestId("submit-button").click();
        cy.getByTestId("zip-download-button").click();
        cy.readFile('cypress/downloads/11000.zip').should('exist');
    })

    it('should display Restultate text when input is valid', () => {
        cy.getByTestId("exam-number-input").type('11000');
        cy.getByTestId("submit-button").click();
        cy.getByTestId("result-text").should('exist');
    });

    it('should not display Restultate text when exam number is invalid', () => {
        cy.getByTestId("exam-number-input").type('123');
        cy.getByTestId("submit-button").click();
        cy.getByTestId("result-text").should('not.exist');
    });
});
