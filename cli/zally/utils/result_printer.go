package utils

import (
	"bytes"
	"fmt"
	"io"
	"strings"

	"github.com/logrusorgru/aurora"
	"github.com/zalando/zally/cli/zally/domain"
)

// ResultPrinter helps to print results to the CLI
type ResultPrinter struct {
	buffer io.Writer
}

// NewResultPrinter creates an instance of ResultPrinter
func NewResultPrinter(buffer io.Writer) ResultPrinter {
	var resultPrinter ResultPrinter
	resultPrinter.buffer = buffer
	return resultPrinter
}

// PrintRules prints a list of supported rules
func (r *ResultPrinter) PrintRules(rules *domain.Rules) {
	r.printRules(rules.Must())
	r.printRules(rules.Should())
	r.printRules(rules.May())
	r.printRules(rules.Hint())
}

func (r *ResultPrinter) printRules(rules []domain.Rule) {
	for _, rule := range rules {
		r.printRule(&rule)
	}
}

func (r *ResultPrinter) printRule(rule *domain.Rule) {
	colorize := r.colorizeByTypeFunc(rule.Type)
	fmt.Fprintf(
		r.buffer,
		"%s %s: %s\n\t%s\n\n",
		colorize(rule.Code),
		colorize(rule.Type),
		rule.Title,
		rule.URL)
}

// PrintViolations creates string representation of Violation
func (r *ResultPrinter) PrintViolations(v *domain.Violations) {
	r.printViolations("MUST", v.Must())
	r.printViolations("SHOULD", v.Should())
	r.printViolations("MAY", v.May())
	r.printViolations("HINT", v.Hint())

	if len(v.Violations) > 0 {
		fmt.Fprint(r.buffer, r.formatHeader("Summary:"))
		fmt.Fprint(r.buffer, r.formatViolationsCount(&v.ViolationsCount))
	}
	r.printServerMessage(v.Message)
}

func (r *ResultPrinter) printViolations(header string, violations []domain.Violation) {
	if len(violations) > 0 {
		fmt.Fprint(r.buffer, r.formatHeader(header))
		for _, violation := range violations {
			fmt.Fprint(r.buffer, r.formatViolation(&violation))
		}
	}
}

func (r *ResultPrinter) printServerMessage(message string) {
	if message != "" {
		fmt.Fprintf(r.buffer, "\n\n%s%s\n\n\n", r.formatHeader("Server message:"), aurora.Green(message))
	}
}

func (r *ResultPrinter) colorizeByTypeFunc(ruleType string) func(interface{}) aurora.Value {
	switch ruleType {
	case "MUST":
		return aurora.Red
	case "SHOULD":
		return aurora.Brown
	case "MAY":
		return aurora.Green
	case "HINT":
		return aurora.Cyan
	default:
		return aurora.Gray
	}
}

func (r *ResultPrinter) formatHeader(header string) string {
	if len(header) == 0 {
		return ""
	}
	return fmt.Sprintf("%s\n%s\n\n", header, strings.Repeat("=", len(header)))
}

func (r *ResultPrinter) formatViolation(v *domain.Violation) string {
	var buffer bytes.Buffer

	colorize := r.colorizeByTypeFunc(v.ViolationType)

	fmt.Fprintf(&buffer, "%s %s\n", colorize(v.ViolationType), colorize(v.Title))
	fmt.Fprintf(&buffer, "\t%s\n", v.Decription)
	fmt.Fprintf(&buffer, "\t%s\n", v.RuleLink)

	for _, path := range v.Paths {
		fmt.Fprintf(&buffer, "\t\t%s\n", path)
	}

	fmt.Fprintf(&buffer, "\n")

	return buffer.String()
}

func (r *ResultPrinter) formatViolationsCount(v *domain.ViolationsCount) string {
	var buffer bytes.Buffer
	fmt.Fprintf(&buffer, "MUST violations: %d\n", v.Must)
	fmt.Fprintf(&buffer, "SHOULD violations: %d\n", v.Should)
	fmt.Fprintf(&buffer, "MAY violations: %d\n", v.May)
	fmt.Fprintf(&buffer, "HINT violations: %d\n", v.Hint)
	return buffer.String()
}
