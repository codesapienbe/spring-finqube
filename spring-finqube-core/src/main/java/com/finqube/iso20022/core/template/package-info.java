package com.finqube.iso20022.core.template;

/**
 * ISO 20022 message processing template and facade classes.
 *
 * <p>This package contains the main template classes that provide a high-level
 * API for sending and processing ISO 20022 financial messages.</p>
 *
 * <h2>Key Classes</h2>
 *
 * <ul>
 *   <li><strong>Iso20022Template</strong> - Main facade for message processing</li>
 * </ul>
 *
 * <h2>Usage</h2>
 *
 * <p>The template classes provide a simple, consistent API for working with
 * ISO 20022 messages, abstracting away the complexity of validation, transport,
 * and error handling.</p>
 *
 * <pre>{@code
 * @Autowired
 * private Iso20022Template iso20022Template;
 *
 * // Send XML message
 * String messageId = iso20022Template.sendMessage(xmlContent);
 *
 * // Send BaseMessage object
 * String messageId = iso20022Template.send(pain001Message);
 * }</pre>
 *
 * <h2>Features</h2>
 *
 * <ul>
 *   <li>Simple message sending API</li>
 *   <li>Automatic message validation</li>
 *   <li>Transport layer abstraction</li>
 *   <li>Comprehensive error handling</li>
 *   <li>Message correlation and tracking</li>
 * </ul>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
